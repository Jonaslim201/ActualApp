package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Random;

public class RegisterFirestore extends Firestore{

    //Start of register user function
    public static void registerUser(Map user, Context activity, FirestoreCallBack callback){
        //Gets inputted username
        String username = (String) user.get("username");
        Log.d("Debug", username);

        //Check if the username exists
        Log.d("Debug", "Checking user");
        Firestore.checkUser(username, activity, user, callback, true);
    }

    static void handleRegistration(Context activity, Map user, String username, FirestoreCallBack callback) {
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
        getID(id, new FirestoreCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success) {
                    // Call the method recursively if randomly generated ID exists
                    checkIDRecursively(activity, user, username, callback);
                } else {
                    //Puts id in the HashMap with the other information and completes Registration
                    user.put("id", id);
                    completeRegistration(activity, user, username, callback);
                }
            }
        });
    }

    //Checks if generated ID already exists in the Firestore
    private static void getID(String id, FirestoreCallBack callBack){
        Query query = db.collection("appUsers").whereEqualTo("id", id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            private boolean uniqueIdFound = true;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("Debug", "Entered onComplete for getID");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //id already exists
                            callBack.onFirestoreResult(true);
                            return;
                        }
                    }
                }
                //id does not exist
                callBack.onFirestoreResult(false);
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

}
