package com.example.actualapp;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


//User class with static fields to allow the information to be accessed throughout the app session
public class User {
    private static String username;
    private static String password;
    private static String email;

    //Unique ID, will be used as a add friend code
    private static String id;

    //Reference to the user document in Firestore
    private static DocumentSnapshot userDoc;

    private static ArrayList<DocumentReference> friends;
    private static ArrayList<String> friendRequests;

    public User(String username, String password, String email, String id){
        User.username = username;
        User.password = password;
        User.email = email;
        User.id = id;
    }

    public User(){
    }

    public static void setUserDoc(DocumentSnapshot userDoc) {
        User.userDoc = userDoc;
    }

    public static DocumentSnapshot getUserDoc() {
        return userDoc;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static void setPassword(String password) {
        User.password = password;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static String getPassword(){
        return password;
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getId() {
        return id;
    }




}
