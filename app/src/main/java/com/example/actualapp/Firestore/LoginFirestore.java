package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

public class LoginFirestore extends Firestore{

    //Start of Login function
    public static void loginUser(Map user, Context activity, FirestoreCallBack callback) {
        String username = (String) user.get("username");
        Log.d("Debug", username);
        Log.d("Debug", "Checking user");

        //Checks if username exists in the Firestore
        checkUser(username, activity, user, callback, false);
    }


    //Checks password and makes Toast if failed to login. Send back false on callback
    static void checkPassword(Context activity, Map user, FirestoreCallBack callback) {
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
}
