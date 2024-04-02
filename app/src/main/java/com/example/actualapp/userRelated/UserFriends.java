package com.example.actualapp.userRelated;

import com.example.actualapp.FeedActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserFriends extends User {
    private static Map<String, DocumentSnapshot> friends;
    private static Map<String, DocumentReference> receivedFriendRequests;

    private static Map<String, DocumentReference> sentFriendRequests;
    private static String fcmToken;

    public static void setFriends(ArrayList<DocumentReference> friends) {
        if (UserFriends.friends == null){
            UserFriends.friends = new HashMap<>();
        }
        for (DocumentReference friendDoc:friends){
            friendDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String id = documentSnapshot.get("id").toString();
                        UserFriends.friends.put(id, documentSnapshot);
                    }
                }
            });
        }
    }

    public static void addFriend(DocumentReference newFriend){
        newFriend.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String id = documentSnapshot.get("id").toString();
                    UserFriends.friends.put(id, documentSnapshot);
                }
            }
        });
    }

    public static Map<String, DocumentSnapshot> getFriends() {
        return friends;
    }

    public static Map<String, DocumentReference> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    //Received Keys are the usernames of the people who sent the request
    public static void setReceivedFriendRequests(ArrayList<DocumentReference> receivedFriendRequests) {
        UserFriends.receivedFriendRequests = new HashMap<>();

        for (DocumentReference friendDoc: receivedFriendRequests){
            UserFriends.receivedFriendRequests.put(friendDoc.getId(), friendDoc);
        }
        FeedActivity.updateFriendRequest();
    }


    //Sent Keys are the unique ID values of the people the user has sent a request to
    public static void setSentFriendRequests(ArrayList<DocumentReference> sentFriendRequests) {
        UserFriends.sentFriendRequests = new HashMap<>();

        for (DocumentReference friendDoc: sentFriendRequests){
            friendDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String id = documentSnapshot.get("id").toString();
                        UserFriends.sentFriendRequests.put(id, friendDoc);
                    }
                }
            });
        }
    }

    public static Map<String, DocumentReference> getSentFriendRequests() {
        return sentFriendRequests;
    }
}
