package com.example.actualapp.Firestore;

import android.util.Log;

import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FriendFirestore extends Firestore {

    FriendFirestore(){

    }

    //Sends a friend request to the user with the given id
    public static void sendFriendRequest(String id, FirestoreCallBack callBack) {

        db.collection("appUsers").whereEqualTo("id", id).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot requestedFriendDocument: task.getResult()){
                    requestedFriendDocument.getReference().update("friendRequests.received", FieldValue.arrayUnion(User.getUserDoc()))
                            .addOnSuccessListener(unused -> {
                        User.getUserDoc().update("friendRequests.sent", FieldValue.arrayUnion(requestedFriendDocument.getReference()));
                        UserFriends.newSentFriendReq(requestedFriendDocument.getReference(), id);
                        callBack.onFirestoreResult(true);

                    });
                }
            } else {
                callBack.onFirestoreResult(false);
            }
        });
    }

    //Accepts the friend request from the user with the given id
    public static void acceptFriendRequest(DocumentReference acceptedFriendDoc, FirestoreCallBack callBack, String acceptedId){
        Log.d("FriendFirestore", "acceptFriendRequest:" + acceptedFriendDoc.getId() + " " + acceptedId);
        if (acceptedFriendDoc != null){
            User.getUserDoc().update("friends", FieldValue.arrayUnion(acceptedFriendDoc)).addOnSuccessListener(unused -> {
                addSelfInFriend(acceptedFriendDoc, callBack);
                ExerciseFirestore.addedFriendLeaderboard(acceptedFriendDoc, acceptedId);
                callBack.onFirestoreResult(true);
            }).addOnFailureListener(e -> callBack.onFirestoreResult(false));
        }
    }

    //Adds the current user to the friend list of the new friend
    public static void addSelfInFriend(DocumentReference acceptedFriendDoc, FirestoreCallBack callBack){
        if (acceptedFriendDoc != null){
            acceptedFriendDoc.update("friends", FieldValue.arrayUnion(User.getUserDoc())).addOnSuccessListener(unused -> Log.d("FriendFirestore", "addSelfInFriend: " + "Success"))
                    .addOnFailureListener(e -> callBack.onFirestoreResult(false));
        }
    }

    //Removes the friend request from the user received friend request array and the friend's sent request array if the request is rejected
    public static void removeFriendRequest(DocumentReference rejectedFriendDoc, FirestoreCallBack callBack){
        if (rejectedFriendDoc != null){
            User.getUserDoc().update("friendRequests.received", FieldValue.arrayRemove(rejectedFriendDoc)).addOnSuccessListener(unused -> {
                rejectedFriendDoc.update("friendRequests.sent", FieldValue.arrayRemove(User.getUserDoc()));
                callBack.onFirestoreResult(true);
            }).addOnFailureListener(e -> callBack.onFirestoreResult(false));
        }
    }

}

