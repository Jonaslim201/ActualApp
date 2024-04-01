package com.example.actualapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Object;
import java.util.Objects;
import java.util.Random;



public class Firestore implements FirestoreCallBack {
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db;
    private static holdDocument userDocument;

    Firestore(){

    }

    //Initializes Firestore
    private static void initializeDatabase(Context activity){
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getDb(){
        return db;
    }

    //Start of register user function
    public static void registerUser(Map user, Context activity, FirestoreCallBack callback, boolean Registering){
        //Gets inputted username
        String username = (String) user.get("username");
        Log.d("Debug", username);

        //Check if the username exists
        Log.d("Debug", "Checking user");
        checkUser(username, activity, user, callback, Registering);
    }

    //Filters if username exists or no
    private static void handleRegistration(Context activity, Map user, String username, FirestoreCallBack callback) {
        Log.d("register", "entered handle register");

        //If username exists, return False on callback
        if (userDocument.isDocumentFound()){
            Log.d("register", "Username exists");
            callback.onFirestoreResult(false);
            Toast.makeText(activity, "Username already exists.", Toast.LENGTH_SHORT).show();
        } else { //If username does noe exist, get unique ID for user
            checkIDRecursively(activity, user, username, callback);
        }
    }

    //Finding unique ID
    private static void checkIDRecursively(Context activity, Map user, String username, FirestoreCallBack callback) {
        final String id = generateId();
        getID(user, id, new FirestoreCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success) {
                    Log.d("hi", "FUCK id exists L");
                    // Call the method recursively if randomly generated ID exists
                    checkIDRecursively(activity, user, username, callback);
                } else {
                    Log.d("hi", "id DONT EXIST BABY WOOOOOOOOOO");

                    //Puts id in the HashMap with the other information and completes Registration
                    user.put("id", id);
                    completeRegistration(activity, user, username, callback);
                }
            }
        });
    }

    //Checks if generated ID already exists in the Firestore
    private static void getID(Map user, String id, FirestoreCallBack callBack){
        Query query = db.collection("appUsers").whereEqualTo("id", id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            private boolean uniqueIdFound = true;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("Debug", "Entered onComplete for getID");
                if (task.isSuccessful()) {
                    Log.d("Debug", "Task was successful for getID");
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("Debug", "Looping through documents");
                        if (document.exists()) {
                            Log.d("Debug", "ID already exists");
                            //If ID exists, returns true on callback and continue recursion
                            callBack.onFirestoreResult(true);
                            break;
                        }
                    }
                    callBack.onFirestoreResult(false);
                } else {
                    Log.d("hi", "id dont exists SHEESH");
                    // Moves on with registration if ID does not exist
                    callBack.onFirestoreResult(false);
                }
            }
        });
    }

    //Adds all the User information to the Firestore and creates the Static User object
    private static void completeRegistration(Context activity, Map user, String username, FirestoreCallBack callback){

        //Adds the Map into Firestore
        db.collection("appUsers").document(username).set(user)
                .continueWithTask(new Continuation<Void, Task<DocumentSnapshot>>() {
                    @Override
                    public Task<DocumentSnapshot> then(@NonNull Task<Void> task) throws Exception {
                        // After setting the document, fetch it to get the updated data
                        return db.collection("appUsers").document(username).get();
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            userDocument.setFoundDocument(task.getResult());
                            Log.d("register", "Added");
                            Toast.makeText(activity, "You have registered successfully!", Toast.LENGTH_SHORT).show();

                            //Creates a Static User object to be used throughout the session of the app
                            Firestore.initializeUserObject(activity, user, true, callback);
                        } else {
                            Log.e("register", "Failed to add user", task.getException());
                            Toast.makeText(activity, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                            callback.onFirestoreResult(false);
                        }
                    }
                });
    }

    //Randomly generates 6-character AlphaNum ID
    private static String generateId(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    //Start of Login function
    public static void loginUser(Map user, Context activity, FirestoreCallBack callback, boolean Registering) {
        String username = (String) user.get("username");
        Log.d("Debug", username);
        Log.d("Debug", "Checking user");

        //Checks if username exists in the Firestore
        checkUser(username, activity, user, callback, Registering);
    }


    //Checks password and makes Toast if failed to login. Send back false on callback
    private static void checkPassword(Context activity, Map user, FirestoreCallBack callback) {
        Log.d("Firestore", String.valueOf(userDocument.isDocumentFound()));
        if (userDocument.isDocumentFound()) {
            try {
                String actualPw = String.valueOf(userDocument.getFoundDocument().get("password"));
                String inputPw = String.valueOf(user.get("password"));
                if (inputPw.equals(actualPw)) {
                    initializeUserObject(activity, user, false, callback);

                } else {
                    Toast.makeText(activity, "Wrong password.", Toast.LENGTH_SHORT).show();
                    callback.onFirestoreResult(false);
                }

        } catch (IllegalArgumentException e) {
            Toast.makeText(activity, "Please enter a value.", Toast.LENGTH_SHORT).show();
            callback.onFirestoreResult(false);
        }

    } else {
        Toast.makeText(activity, "Username not found.", Toast.LENGTH_SHORT).show();
        callback.onFirestoreResult(false);
        }
    }

    //Initializes the Static User object with all the user information
    private static void initializeUserObject(Context activity, Map user, Boolean registering, FirestoreCallBack callback){
        //Getting reference to the user document
        DocumentSnapshot userDoc = userDocument.getFoundDocument();

        Log.d("Firestore", String.valueOf(user.get("password")));

        //Initializing the User object
        User.setUsername(Objects.requireNonNull(user.get("username")).toString());
        User.setPassword(Objects.requireNonNull(user.get("password")).toString());

        if (registering){
            User.setEmail(Objects.requireNonNull(user.get("email")).toString());
            User.setId(Objects.requireNonNull(user.get("id")).toString());
            userDoc.getReference().update("friends", Collections.emptyList());
        } else {
            User.setEmail((String) userDocument.getFoundDocument().get("email"));
            User.setId((String) userDocument.getFoundDocument().get("id"));
            ArrayList<DocumentReference> friendsList = (ArrayList<DocumentReference>) userDoc.get("friends");
            ArrayList<Object> friendsRequests = (ArrayList<Object>) userDoc.get("friendRequests");

            if (friendsList != null && !friendsList.isEmpty()){
                UserFriends.setFriends(friendsList);
            }

            if (friendsRequests != null && !friendsList.isEmpty()){
                ArrayList<String> requests = new ArrayList<String>();
                for (Object obj:friendsRequests){
                    if (obj instanceof String){
                        requests.add(String.valueOf(obj));
                    }
                }
                UserFriends.setFriendRequests(requests);
            }
        }
        //Setting the document reference in the User Object
        User.setUserDoc(userDoc);



        userDoc.getReference().collection("workouts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            for (String category:ExerciseCategoriesActivity.getExerciseArray()){
                                transaction.set(userDoc.getReference().collection("workouts").document(category), new HashMap<>());
                            }
                            return null;
                        }
                    });
                } else {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> documentData = document.getData();
                        if (documentData != null){
                            for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                                Object value = entry.getValue();
                                // Check if the value is an ArrayList
                                if (value instanceof ArrayList) {
                                    ArrayList<Map<String, Object>> array = (ArrayList<Map<String, Object>>) value;
                                    for (Object obj:array){
                                        if (obj instanceof Map){
                                            Map<String, Object> map = (Map<String, Object>) obj;
                                            for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
                                                String key = mapEntry.getKey();
                                                Object mapValue = mapEntry.getValue();
                                                // Do something with the key-value pair
                                                Log.d("Workout Data", "Key: " + key + ", Value: " + mapValue);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                UserExercise.setWorkoutsDoc(userDoc.getReference().collection("workouts"));
            }
        });

        userDoc.getReference().collection("leaderboards").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            for (String category:ExerciseCategoriesActivity.getExerciseArray()){
                                transaction.set(userDoc.getReference().collection("leaderboards").document(category), new HashMap<>());
                            }
                            return null;
                        }
                    });
                } else {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> documentData = document.getData();
                        if (documentData != null){
                            for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                                Object value = entry.getValue();
                                // Check if the value is an ArrayList
                                if (value instanceof ArrayList) {
                                    ArrayList<Map<String, Object>> array = (ArrayList<Map<String, Object>>) value;
                                    for (Object obj:array){
                                        if (obj instanceof Map){
                                            Map<String, Object> map = (Map<String, Object>) obj;
                                            for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
                                                String key = mapEntry.getKey();
                                                Object mapValue = mapEntry.getValue();
                                                // Do something with the key-value pair
                                                Log.d("Workout Data", "Key: " + key + ", Value: " + mapValue);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                UserExercise.setWorkoutsDoc(userDoc.getReference().collection("workouts"));
            }
        });

        callback.onFirestoreResult(true);
        Toast.makeText(activity, "Login successful.", Toast.LENGTH_SHORT).show();
    }


    //Checks if the username exists in the Firestore
    private static void checkUser(String username, Context activity, Map user, FirestoreCallBack callback, boolean Registering) {
        userDocument = new holdDocument();
        initializeDatabase(activity);
        Query check = db.collection("appUsers").whereEqualTo("username", username);
        Log.d("Debug", "Query gotten");
        check.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("Debug", "Entered onComplete");
                    if (task.isSuccessful()) {
                        Log.d("Debug", "Task was successful");
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
                        handleRegistration(activity, user, username, callback);
                    } else {
                        Log.d("register", "pushed to login");
                        checkPassword(activity, user, callback);
                    }

                }
            });

    }


    @Override
    public void onFirestoreResult(boolean success) {

    }

    //Getting the list of exercises for a category for the RecycleView
    public static void getExercises(Context activity, String category, ExerciseCallBack exerciseCallBack){
        initializeDatabase(activity);
        ArrayList<Exercise> exercises = new ArrayList<>();
        db.collection("ExerciseCategories").document(category).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                for(Map.Entry<String, Object> entry: map.entrySet()){
                    ArrayList<String> exercise = (ArrayList<String>) entry.getValue();
                    exercises.add(new Exercise(exercise.get(0), exercise.get(1), exercise.get(2)));
                    exerciseCallBack.onSuccessResult(exercises);
                }
            }
        });
    }

    public static void insertWorkout(String category, Workout workout, FirestoreCallBack callBack){
        UserExercise.getWorkoutsDoc().document(category).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot currCategoryDoc = task.getResult();
                    List<Object> toAddWorkouts = new ArrayList<>(Collections.singletonList(workout));

                    if (currCategoryDoc.contains(workout.getName())){
                        List<Object> existingWorkouts = (List<Object>) currCategoryDoc.get(workout.getName());
                        Number existingHighScore = (Number) existingWorkouts.get(0);
                        float highScore = existingHighScore.floatValue();
                        if (workout.getWeightLifted() >= highScore){
                            highScore = workout.getWeightLifted();
                            existingWorkouts.remove(0);
                            existingWorkouts.add(0, highScore);
                        }
                        existingWorkouts.addAll(toAddWorkouts);
                        UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), existingWorkouts).addOnSuccessListener(unused -> callBack.onFirestoreResult(true))
                                .addOnFailureListener(e -> callBack.onFirestoreResult(false));

                    } else {
                        toAddWorkouts.add(0,workout.getWeightLifted());
                        UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), toAddWorkouts).addOnSuccessListener(unused -> callBack.onFirestoreResult(true))
                                .addOnFailureListener(e -> callBack.onFirestoreResult(false));
                    }
                }
            }
        });

    }
}

