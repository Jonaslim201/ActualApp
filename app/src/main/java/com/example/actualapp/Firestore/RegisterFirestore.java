package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.Query;

import java.util.Map;
import java.util.Random;

public class RegisterFirestore extends Firestore{

    private RegisterFirestore(){
    }

    //Start of register user function
    public static void registerUser(Map<String, Object> user, Context activity, FirestoreCallBack callback){
        //Check if the username exists
        Log.d("Debug", "Checking user");
        Firestore.checkUser((String) user.get("username"), activity, user, callback, true);
    }

    static void handleRegistration(Context activity, Map<String, Object> user, String username, FirestoreCallBack callback) {
        //If username exists, return False on callback
        if (userDocument.isDocumentFound()){
            Log.d("register", "Username exists");
            callback.onFirestoreResult(false);
            Toast.makeText(activity, "Username already exists.", Toast.LENGTH_SHORT).show();
        } else { //If username does not exist, get unique ID for user
            checkIDRecursively(activity, user, username, callback);
        }
    }

    //Finding unique ID
    private static void checkIDRecursively(Context activity, Map<String, Object> user, String username, FirestoreCallBack callback) {
        final String id = generateId();
        getID(id, success -> {
            if (success) {
                checkIDRecursively(activity, user, username, callback);
            } else {
                user.put("id", id);
                completeRegistration(activity, user, username, callback);
            }
        });
    }

    //Checks if generated ID already exists in the Firestore
    private static void getID(String id, FirestoreCallBack callBack){
        Query query = db.collection("appUsers").whereEqualTo("id", id);
        query.get().addOnCompleteListener(task -> callBack.onFirestoreResult(task.isSuccessful() && !task.getResult().isEmpty()));
    }

    //Adds all the User information to the Firestore and creates the Static User object
    private static void completeRegistration(Context activity, Map<String, Object> user, String username, FirestoreCallBack callback){
        db.collection("appUsers").document(username).set(user)
                .continueWithTask(task -> db.collection("appUsers").document(username).get())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userDocument.setFoundDocument(task.getResult());
                        Toast.makeText(activity, "You have registered successfully!", Toast.LENGTH_SHORT).show();
                        Firestore.initializeUserObject(user, true, callback);
                    } else {
                        Toast.makeText(activity, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                        callback.onFirestoreResult(false);
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
