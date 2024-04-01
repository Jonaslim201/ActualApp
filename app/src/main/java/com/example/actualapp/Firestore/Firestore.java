package com.example.actualapp.Firestore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.actualapp.ExerciseCategoriesActivity;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.userRelated.UserFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Object;


public class Firestore implements FirestoreCallBack {
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db;
    static holdDocument userDocument;

    Firestore(){

    }

    //Initializes Firestore
    static void initializeDatabase(Context activity){
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
    }


    //Initializes the Static User object with all the user information
    static void initializeUserObject(Context activity, Map user, Boolean registering, FirestoreCallBack callback){
        //Getting reference to the user document
        DocumentSnapshot userDoc = userDocument.getFoundDocument();

        Log.d("Firestore", String.valueOf(user.get("password")));

        //Initializing the User object
        if (registering){
            User currUser = new User.UserBuilder().setUsername(user.get("username").toString())
                                                  .setPassword(user.get("password").toString())
                                                  .setEmail(user.get("email").toString())
                                                  .setId(user.get("id").toString())
                                                  .setUserDoc(userDoc).build();

            userDoc.getReference().update("friends", Collections.emptyList());
            userDoc.getReference().update("friendRequests", Collections.emptyList());
        } else {
            User currUser = new User.UserBuilder().setUsername(user.get("username").toString())
                                                  .setPassword(user.get("password").toString())
                                                  .setEmail((String) userDocument.getFoundDocument().get("email"))
                                                  .setId((String) userDocument.getFoundDocument().get("id"))
                                                  .setUserDoc(userDoc).build();

            ArrayList<DocumentReference> friendsList = (ArrayList<DocumentReference>) userDoc.get("friends");
            ArrayList<Object> friendsRequests = (ArrayList<Object>) userDoc.get("friendRequests");

            if (friendsList != null && !friendsList.isEmpty()){
                UserFriends.setFriends(friendsList);
            }

            if (friendsRequests != null && !friendsRequests.isEmpty()){
                FriendFirestore.initializeFriendReqListener();
                ArrayList<DocumentReference> requests = new ArrayList<>();
                for (Object obj:friendsRequests){
                    if (obj instanceof DocumentReference){
                        requests.add((DocumentReference) obj);
                    }
                }
                Log.d("Firestore", requests.toString());
                UserFriends.setFriendRequests(requests);
            }
        }

        List<String> collectionNames = Arrays.asList("workouts", "leaderboards");

        for (String collectionName:collectionNames){
            userDoc.getReference().collection(collectionName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){
                        createFirestoreDocuments(userDoc.getReference().collection(collectionName));
                    } else {
                        getFirestoreDocuments(queryDocumentSnapshots);
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (collectionName.equals("workouts")){
                        UserExercise.setWorkoutsDoc(userDoc.getReference().collection(collectionName));
                    }
                }
            });
        }

        callback.onFirestoreResult(true);
        Toast.makeText(activity, "Login successful.", Toast.LENGTH_SHORT).show();
    }

    private static void createFirestoreDocuments(CollectionReference collectionRef) {
        db.runTransaction(transaction -> {
            for (String category : ExerciseCategoriesActivity.getExerciseArray()) {
                transaction.set(collectionRef.document(category), new HashMap<>());
            }
            return null;
        });
    }

    private static void getFirestoreDocuments(QuerySnapshot querySnapshot) {
        for (QueryDocumentSnapshot document : querySnapshot) {
            Map<String, Object> documentData = document.getData();
            if (documentData != null) {
                for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof ArrayList) {
                        getFirestoreData((ArrayList<?>) value);
                    }
                }
            }
        }
    }

    private static void getFirestoreData(ArrayList<?> arrayList) {
        for (Object obj : arrayList) {
            if (obj instanceof Map) {
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


    //Checks if the username exists in the Firestore
    static void checkUser(String username, Context activity, Map user, FirestoreCallBack callback, boolean Registering) {
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
                        RegisterFirestore.handleRegistration(activity, user, username, callback);
                    } else {
                        Log.d("register", "pushed to login");
                        LoginFirestore.checkPassword(activity, user, callback);
                    }

                }
            });

    }


    @Override
    public void onFirestoreResult(boolean success) {

    }
}

