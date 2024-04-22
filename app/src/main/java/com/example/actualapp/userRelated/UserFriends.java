package com.example.actualapp.userRelated;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class UserFriends extends User {
    private static Map<String, DocumentSnapshot> friendDocuments;
    private static ArrayList<Friend> friend;
    private static Map<String, DocumentReference> receivedFriendRequests;
    private static ArrayList<Friend> receivedFriendRequestsList;
    private static Map<String, DocumentReference> sentFriendRequests;
    private static String fcmToken;

    public static void initializeFriendDocuments(){
        friend = new ArrayList<>();
    }


    public static void setFriendDocuments(ArrayList<DocumentReference> friends, FirestoreCallBack callBack){
        if (UserFriends.friendDocuments == null){
            UserFriends.friendDocuments = new HashMap<>();
        }

        CountDownLatch internalLatch = new CountDownLatch(friends.size());

        if (friends.size() > UserFriends.friend.size()){
            for (DocumentReference friendDoc:friends){
                friendDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String id = documentSnapshot.get("id").toString();
                            UserFriends.friendDocuments.put(id, documentSnapshot);
                            Friend friend = new Friend.FriendBuilder().setId(id)
                                    .setUsername(documentSnapshot.get("username").toString()).Build();
                            UserFriends.friend.add(friend);
                        }

                        internalLatch.countDown();

                        if (internalLatch.getCount() == 0){
                            callBack.onFirestoreResult(true);
                        }
                    }
                });
            }
        }
    }

    public static void addFriend(DocumentReference newFriend, FirestoreCallBack callBack){
        Log.d("UserFriends", "addFriend: " + newFriend.getId());
        Log.d("UserFriends", "addFriend: " + friendDocuments.keySet());
        if (friendDocuments == null){
            friendDocuments = new HashMap<>();
        }

        CountDownLatch internalLatch = new CountDownLatch(1);

        newFriend.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String id = documentSnapshot.get("id").toString();
                    UserFriends.friendDocuments.put(id, documentSnapshot);
                    Friend newFriend = new Friend(documentSnapshot.get("username").toString(), id);
                    UserFriends.friend.add(newFriend);
                    internalLatch.countDown();
                }

                try{
                    internalLatch.await();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                callBack.onFirestoreResult(true);
            }
        });
    }

    public static void setFriend(ArrayList<Friend> friend) {
        UserFriends.friend = friend;
    }

    public static ArrayList<Friend> getFriend() {
        if (friend == null || friend.isEmpty()){
            return new ArrayList<>();
        } else {
            return friend;
        }
    }

    public static Map<String, DocumentSnapshot> getFriendDocuments() {
        if (friendDocuments == null) {
            friendDocuments = new HashMap<>();
        }
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
        Log.d("UserFriends", "setReceivedFriendRequests");
        UserFriends.receivedFriendRequests = new HashMap<>();
        UserFriends.receivedFriendRequestsList = new ArrayList<>();

        CountDownLatch internalLatch = new CountDownLatch(receivedFriendRequests.size());


        for (DocumentReference friendDoc: receivedFriendRequests){
            UserFriends.receivedFriendRequests.put(friendDoc.getId(), friendDoc);
            Log.d("UserFriendsMapping", "setReceivedFriendRequests: " + friendDoc.getId());
            friendDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()){
                            String id = documentSnapshot.get("id").toString();
                            Friend friend = new Friend.FriendBuilder().setId(id)
                                                                    .setUsername(documentSnapshot.get("username").toString()).Build();
                            Log.d("UserFriends", "setReceivedFriendRequests: " + friend.getUsername() + " " + friend.getId());
                            UserFriends.receivedFriendRequestsList.add(friend);
                        }
                    }

                    internalLatch.countDown();
                    if (internalLatch.getCount() == 0) {
                        callBack.onFirestoreResult(true);
                    }
                }
            });
        }
    }

    public static void deleteFriendRequest(DocumentReference receivedFriendRequest, FirestoreCallBack callBack){
        if (UserFriends.receivedFriendRequests != null && receivedFriendRequest != null){
            String idToDelete = receivedFriendRequest.getId();
            UserFriends.receivedFriendRequests.remove(idToDelete);
            Log.d("UserFriends", "deleteFriendRequest: " + idToDelete);
            Log.d("UserFriends", "deleteFriendRequest: " + UserFriends.receivedFriendRequestsList.size());
            UserFriends.receivedFriendRequestsList.removeIf(friend -> friend.getUsername().equals(idToDelete));
            Log.d("UserFriends", "deleteFriendRequest: " + UserFriends.receivedFriendRequestsList.size());
            callBack.onFirestoreResult(true);
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

    public static void newSentFriendReq(DocumentReference friendDoc, String id){
        sentFriendRequests.put(id, friendDoc);
    }

    public static Map<String, DocumentReference> getSentFriendRequests() {
        return sentFriendRequests;
    }
}
