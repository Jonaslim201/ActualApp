package com.example.actualapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Firestore implements LoginCallBack{
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db;
    private holdDocument userDocument;

    private void initializeDatabase(Context activity){
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
        Log.d("Debug", db.toString());
    }

    public void registerUser(Map user, Context activity, LoginCallBack callback, boolean Registering){
        String username = (String) user.get("username");
        Log.d("Debug", username);
        Log.d("Debug", "Checking user");
        checkUser(username, activity, user, callback, Registering);
    }

    private void handleRegistration(Context activity, Map user, String username, LoginCallBack callback) {
        Log.d("register", "entered handle register");
        if (userDocument.isDocumentFound()){
            Log.d("register", "Username exists");
            callback.onLoginResult(false);
            Toast.makeText(activity, "Username already exists.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("register", "Username does not exist");
            db.collection("appUsers").document(username).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void v) {
                    Log.d("register", "Added");
                    Toast.makeText(activity, "You have registered successfully!.", Toast.LENGTH_SHORT).show();
                    callback.onLoginResult(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("register", "Failed to add");
                    Toast.makeText(activity, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                    callback.onLoginResult(false);
                }
            });
        }
    }

    public void loginUser(Map user, Context activity, LoginCallBack callback, boolean Registering) {
        String username = (String) user.get("username");
        Log.d("Debug", username);
        Log.d("Debug", "Checking user");
        checkUser(username, activity, user, callback, Registering);
    }



    private void handleLogin(Context activity, Map user, LoginCallBack callback) {
        if (userDocument.isDocumentFound()) {
            try {
                if (userDocument.getFoundDocument().get("password").equals(user.get("password"))) {
                    Toast.makeText(activity, "Login successful.", Toast.LENGTH_SHORT).show();
                    callback.onLoginResult(true);
                } else {
                    Toast.makeText(activity, "Wrong password.", Toast.LENGTH_SHORT).show();
                    callback.onLoginResult(false);
                }
            } catch (NullPointerException e) {
                Toast.makeText(activity, "Please enter a value.", Toast.LENGTH_SHORT).show();
                callback.onLoginResult(false);
            }
        } else {
            Toast.makeText(activity, "Username not found.", Toast.LENGTH_SHORT).show();
            callback.onLoginResult(false);
        }
    }


    private void checkUser(String username, Context activity, Map user, LoginCallBack callback, boolean Registering) {
        this.userDocument = new holdDocument();
        Firestore outerInstance = this;
        initializeDatabase(activity);
        Query check = db.collection("appUsers").whereEqualTo("username", username);
        Log.d("Debug", db.collection("appUsers").document(username).toString());
        Log.d("Debug", "Query gotten");
        check.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("Debug", "Entered onComplete");
                    if (task.isSuccessful()) {
                        Log.d("Debug", "Task was successful");
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d("Debug", "entered here");
                            Log.d("Debug", document.getString("password"));
                            Log.d("Debug", String.valueOf(document.exists()));
                            Log.d("Debug", "exited here");
                            if (document.exists()) {
                                Log.d("Debug", "name already exists");
                                outerInstance.userDocument.setFoundDocument(document);
                                break;
                            }
                        }
                    }
                    if(Registering) {
                        Log.d("register", "pushed to handleRegistration");
                        handleRegistration(activity, user, username, callback);
                    } else {
                        Log.d("register", "pushed to login");
                        handleLogin(activity, user, callback);
                    }

                }
            });

    }

    @Override
    public void onLoginResult(boolean success) {

    }
}

