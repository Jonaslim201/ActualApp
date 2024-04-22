package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

public class LoginFirestore extends Firestore{

    private LoginFirestore(){
    }

    //Start of Login function
    public static void loginUser(Map<String, Object> user, Context activity, FirestoreCallBack callback) {
        Log.d("Debug", "Checking user");
        //Checks if username exists in the Firestore
        checkUser((String) user.get("username"), activity, user, callback, false);
    }


    //Checks password and makes Toast if failed to login. Send back false on callback
    static void checkPassword(Context activity, Map<String, Object> user, FirestoreCallBack callback) {
        Log.d("Firestore", String.valueOf(userDocument.isDocumentFound()));
        if (userDocument.isDocumentFound()) {
            try {
                String actualPw = String.valueOf(userDocument.getFoundDocument().get("password"));
                String inputPw = String.valueOf(user.get("password"));
                if (inputPw.equals(actualPw)) {
                    initializeUserObject(user, false, callback);
                } else {
                    displayToast(activity, "Wrong password.", callback);
                }

            } catch (IllegalArgumentException e) {
                displayToast(activity, "Please enter a value.", callback);
            }

        } else {
            displayToast(activity, "Username not found.", callback);
        }
    }

    static void displayToast(Context activity, String message, FirestoreCallBack callback) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        callback.onFirestoreResult(false);
    }
}
