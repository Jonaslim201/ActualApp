package com.example.actualapp;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class UserFriends extends User{
    private static ArrayList<DocumentReference> friends;
    private static ArrayList<String> friendRequests;

    public static void setFriends(ArrayList<DocumentReference> friends) {
        UserFriends.friends = friends;
    }

    public static ArrayList<DocumentReference> getFriends() {
        return friends;
    }

    public static ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public static void setFriendRequests(ArrayList<String> friendRequests) {
        UserFriends.friendRequests = friendRequests;
    }
}
