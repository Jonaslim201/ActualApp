package com.example.actualapp.userRelated;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class UserFriends extends User {
    private static ArrayList<DocumentReference> friends;
    private static ArrayList<DocumentReference> friendRequests;
    private static String fcmToken;

    public static void setFriends(ArrayList<DocumentReference> friends) {
        UserFriends.friends = friends;
    }

    public static ArrayList<DocumentReference> getFriends() {
        return friends;
    }

    public static ArrayList<DocumentReference> getFriendRequests() {
        return friendRequests;
    }

    public static void setFriendRequests(ArrayList<DocumentReference> friendRequests) {
        UserFriends.friendRequests = friendRequests;
    }

}
