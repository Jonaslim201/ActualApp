package com.example.actualapp.Firestore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.example.actualapp.fragments.HomeFragment;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.userRelated.UserFriends;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Firestore implements FirestoreCallBack {
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db;
    static holdDocument userDocument;
    static ExecutorService executorService = Executors.newFixedThreadPool(4);

    protected Firestore(){

    }

    //Initializes Firestore
    static void initializeDatabase(Context activity) {
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
    }


    //Initializes the Static User object with all the user information
    static void initializeUserObject(Map<String, Object> user, Boolean registering, FirestoreCallBack callback){
        //Getting reference to the user document
        DocumentReference userDoc = userDocument.getFoundDocument().getReference();
        CountDownLatch latch = new CountDownLatch(6);

        //1. Creating the User object
        if (registering){
            new User.UserBuilder().setUsername(user.get("username").toString())
                    .setPassword(user.get("password").toString())
                    .setEmail(user.get("email").toString())
                    .setId(user.get("id").toString())
                    .setUserDoc(userDoc).build();
        } else {
            new User.UserBuilder().setUsername(user.get("username").toString())
                    .setPassword(user.get("password").toString())
                    .setEmail((String) userDocument.getFoundDocument().get("email"))
                    .setId((String) userDocument.getFoundDocument().get("id"))
                    .setUserDoc(userDoc).build();
        }

        executorService.execute(() -> {
            HomeFragment.initializeFriendWorkouts();
            FirestoreListener.getInstanceListener().feedListener(true, success -> {
                Log.d("Firestore", "Feed set");
                latch.countDown();
            });
        });


        //2. Getting friend list and friend requests
        executorService.execute(() -> {
            ExecutorService innerExecutorService  = Executors.newFixedThreadPool(1);

            innerExecutorService.execute(() -> {
                if (registering){
                    userDoc.update("friends", Collections.emptyList());

                    Map<String, Object> newFriendsReqMap = new HashMap<>();
                    newFriendsReqMap.put("sent", Collections.emptyList());
                    newFriendsReqMap.put("received", Collections.emptyList());

                    db.runTransaction(transaction -> {
                        transaction.set(userDoc.collection("feed").document("feed"), new HashMap());
                        transaction.update(userDoc.collection("feed").document("feed"), "actualFeed", Collections.emptyList());
                        return null;
                    }).addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));

                    userDoc.update("friendRequests", newFriendsReqMap);
                } else {
                    ArrayList<DocumentReference> friendsList = (ArrayList<DocumentReference>) userDocument.getFoundDocument().get("friends");
                    Map<String, Object> friendsRequests = (Map<String, Object>) userDocument.getFoundDocument().get("friendRequests");

                    if (friendsRequests != null && !friendsRequests.isEmpty()){
                        ArrayList<DocumentReference> sentRequests = (ArrayList<DocumentReference>) friendsRequests.get("sent");

                        if (sentRequests == null || sentRequests.isEmpty()){
                            UserFriends.setSentFriendRequests(new ArrayList<>());
                        } else {
                            UserFriends.setSentFriendRequests(sentRequests);
                        }
                    }
                }

                UserFriends.initializeFriendDocuments();

                latch.countDown();
            });

            innerExecutorService.execute(() -> {
                FirestoreListener.getInstanceListener().friendReqListener(true, success -> {
                    Log.d("Firestore", "Friend requests set");
                    latch.countDown();
                });
            });

            innerExecutorService.execute(() -> {
                FirestoreListener.getInstanceListener().friendsListener(true, success -> {
                    Log.d("Firestore", "Friends set");
                    latch.countDown();
                });
            });

            innerExecutorService.shutdown();
        });

        // 3. Getting workout collection
        executorService.execute(() -> {

            userDoc.collection("workouts").get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()){
                    createFirestoreDocuments(userDoc.collection("workouts"));
                    UserExercise.setWorkoutMap(new HashMap<>());
                } else {
                    getFirestoreDocuments(queryDocumentSnapshots, "workouts");
                }
            }).addOnCompleteListener(task -> Log.d("Firestore", "Workout collection set"));
            UserExercise.setWorkoutsDoc(userDoc.collection("workouts"));

            latch.countDown();

        });

        //4. Getting leaderboard collection
        executorService.execute(() -> {

            userDoc.collection("leaderboards").get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()){
                    createFirestoreDocuments(userDoc.collection("leaderboards"));
                    Leaderboard.setFriendWorkoutMap(new HashMap<>());
                } else {
                    getFirestoreDocuments(queryDocumentSnapshots, "leaderboards");
                }
            }).addOnCompleteListener(task -> Log.d("Firestore", "Leaderboard collection set"));
            Leaderboard.setLeaderboardCollection(userDoc.collection("leaderboards"));
            FirestoreListener.getInstanceListener().leaderboardListener(true, success -> {
                Log.d("Firestore", "Leaderboard set");
                latch.countDown();
            });

        });

        executorService.execute(() -> {
            try {
                Log.d("Firestore", "User object waiting");
                latch.await();
                Log.d("Firestore", "User object initialized");
                callback.onFirestoreResult(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });


    }



    private static void createFirestoreDocuments(CollectionReference collectionRef) {
        db.runTransaction(transaction -> {
            for (String category : ExerciseFirestore.getExerciseArray()) {
                transaction.set(collectionRef.document(category), new HashMap<>());
            }
            return null;
        }).addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    private static void getFirestoreDocuments(QuerySnapshot querySnapshot, String collectionName) {

        HashMap<WorkoutKey, List<Workout>> workoutMap = new HashMap<>();
        HashMap<WorkoutKey, ArrayList<FriendWorkout>> friendWorkoutMap = new HashMap<>();
        HashMap<String, DocumentSnapshot> leaderboardDocumentSnapshots = new HashMap<>();

        for (QueryDocumentSnapshot document : querySnapshot) {

            // The document ID is the workout type
            String workoutType = document.getId();
            document.getData().forEach((exerciseName, exerciseData)->{
                if (exerciseData instanceof ArrayList){
                    ArrayList<HashMap<String, Object>> exerciseDataArray = (ArrayList<HashMap<String, Object>>) exerciseData;
                    List<Workout> workouts;

                    workouts = exerciseDataArray.stream().map(Workout::new).collect(Collectors.toList());
                    WorkoutKey key = new WorkoutKey(workoutType, exerciseName);
                    workoutMap.put(key, workouts);

                } else if (exerciseData instanceof Map){
                    leaderboardDocumentSnapshots.put(workoutType, document);
                    Map<String, Object> exerciseDataMap = (Map<String, Object>) exerciseData;
                    ArrayList<FriendWorkout> workouts = new ArrayList<>();
                    WorkoutKey key = new WorkoutKey(workoutType, exerciseName);

                    exerciseDataMap.forEach((userID, friendWorkoutData)->{
                        if (friendWorkoutData instanceof Map){
                            workouts.add(new FriendWorkout((Map<String, Object>) friendWorkoutData));
                        }
                    });
                    friendWorkoutMap.put(key, workouts);
                }

            });
        }

        if(Objects.equals(collectionName, "workouts")){
            UserExercise.setWorkoutMap(workoutMap);
        } else if(Objects.equals(collectionName, "leaderboards")){
            Leaderboard.setFriendWorkoutMap(friendWorkoutMap);
            Leaderboard.setLeaderboardDocumentSnapshots(leaderboardDocumentSnapshots);
        }
    }


    //Checks if the username exists in the Firestore
    static void checkUser(String username, Context activity, Map<String, Object> user, FirestoreCallBack callback, boolean Registering) {
        userDocument = new holdDocument();
        initializeDatabase(activity);
        Query check = db.collection("appUsers").whereEqualTo("username", username);
        check.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.d("Debug", "Task was not successful");
                return;
            } else {
                for (DocumentSnapshot document : task.getResult()) {
                    if (document.exists()) {
                        Log.d("Debug", "name already exists");
                        Firestore.userDocument.setFoundDocument(document);
                        break;
                    }
                }
            }


            if(Registering) {
                Log.d("register", "pushed to handleRegistration");
                RegisterFirestore.handleRegistration(activity, user, username, callback);
            } else {
                Log.d("register", "pushed to login");
                LoginFirestore.checkPassword(activity, user, callback);
            }

        });

    }


    @Override
    public void onFirestoreResult(boolean success) {

    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
}

