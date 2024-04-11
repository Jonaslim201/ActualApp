package com.example.actualapp.userRelated;

import com.example.actualapp.FeedActivity;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserFriends extends User {
    private static Map<String, DocumentSnapshot> friendDocuments;
    private static ArrayList<Friend> friend;
    private static Map<String, DocumentReference> receivedFriendRequests;
    private static ArrayList<Friend> receivedFriendRequestsList;

    private static Map<String, DocumentReference> sentFriendRequests;
    private static String fcmToken;

    public static void setFriendDocuments(ArrayList<DocumentReference> friends){
        if (UserFriends.friendDocuments == null){
            UserFriends.friendDocuments = new HashMap<>();
        }
        for (DocumentReference friendDoc:friends){
            friendDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String id = documentSnapshot.get("id").toString();
                        UserFriends.friendDocuments.put(id, documentSnapshot);
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
                    UserFriends.friendDocuments.put(id, documentSnapshot);
                }
            }
        });
    }

    public static void setFriend(ArrayList<Friend> friend) {
        UserFriends.friend = friend;
    }

    public static ArrayList<Friend> getFriend() {
        return new ArrayList<>();
    }


//    public static void addFriend(DocumentReference newFriend){
//        Log.d("Friend", "add Friend getting called");
//        if (friend == null){
//            friend = new ArrayList<>();
//        }
//
//        newFriend.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()){
//                String newFriendId = documentSnapshot.get("id").toString();
//                boolean isFriendExists = friend.stream().anyMatch(f -> f.getId().equals(newFriendId));
//                if (!isFriendExists){
//                    Friend newFriendObj = new Friend.FriendBuilder().setId(documentSnapshot.get("id").toString())
//                                                                    .setUsername(documentSnapshot.get("username").toString()).Build();
//                    Log.d("Friend", "addFriend: " + documentSnapshot.get("username").toString());
//                    friend.add(newFriendObj);
//                    String id = documentSnapshot.get("id").toString();
//                    UserFriends.friendDocuments.put(id, documentSnapshot);
//                }
//                Log.d("Friend", "addFriends: " + friend.size());
//            }
//        });
//    }

    public static Map<String, DocumentSnapshot> getFriendDocuments() {
        return friendDocuments;
    }

    public static Map<String, DocumentReference> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public static ArrayList<Friend> getReceivedFriendRequestsList() {
        return receivedFriendRequestsList;
    }

    //Received Keys are the usernames of the people who sent the request
    public static void setReceivedFriendRequests(ArrayList<DocumentReference> receivedFriendRequests, FirestoreCallBack callBack) {
        UserFriends.receivedFriendRequests = new HashMap<>();
        UserFriends.receivedFriendRequestsList = new ArrayList<>();

        for (DocumentReference friendDoc: receivedFriendRequests){
            UserFriends.receivedFriendRequests.put(friendDoc.getId(), friendDoc);
        }
        setReceivedFriendRequestsList(callBack);
        FeedActivity.updateFriendRequest();
    }

    public static void setReceivedFriendRequestsList(FirestoreCallBack callBack) {
        if (!UserFriends.receivedFriendRequests.isEmpty()){
            for (Map.Entry<String, DocumentReference> entry: UserFriends.receivedFriendRequests.entrySet()){
                entry.getValue().get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        String id = documentSnapshot.get("id").toString();
                        Friend friend = new Friend.FriendBuilder().setId(id)
                                                                .setUsername(documentSnapshot.get("username").toString()).Build();
                        UserFriends.receivedFriendRequestsList.add(friend);
                    }
                });
            }
        }
        callBack.onFirestoreResult(true);
    }

    public static void deleteFriendRequest(DocumentReference receivedFriendRequest){
        if (UserFriends.receivedFriendRequests != null && receivedFriendRequest != null){
            String idToDelete = receivedFriendRequest.getId();
            UserFriends.receivedFriendRequests.remove(idToDelete);

            UserFriends.receivedFriendRequestsList.removeIf(friend -> friend.getId().equals(idToDelete));
        }
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
