package com.example.actualapp.Firestore;

import androidx.annotation.NonNull;

import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FriendFirestore extends Firestore {

    FriendFirestore(){

    }

    public static void sendFriendRequest(String id, FirestoreCallBack callBack) {
        db.collection("appUsers").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (QueryDocumentSnapshot requestedFriendDocument: task.getResult()){
                            requestedFriendDocument.getReference().update("friendRequests.received", FieldValue.arrayUnion(User.getUserDoc().getReference())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    User.getUserDoc().getReference().update("friendRequests.sent", FieldValue.arrayUnion(requestedFriendDocument.getReference()));
                                    callBack.onFirestoreResult(true);
                                }
                            });
                        }
                    } else {
                        callBack.onFirestoreResult(false);
                    }
                }
            }
        });
    }

    public static void acceptFriendRequest(DocumentReference acceptedFriendDoc, FirestoreCallBack callBack, String acceptedId){
        User.getUserDoc().getReference().update("friends", FieldValue.arrayUnion(acceptedFriendDoc)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                addSelfInFriend(acceptedFriendDoc, callBack);
                ExerciseFirestore.addedFriendLeaderboard(acceptedFriendDoc, acceptedId);
                UserFriends.addFriend(acceptedFriendDoc);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callBack.onFirestoreResult(false);
            }
        });
    }

    public static void addSelfInFriend(DocumentReference acceptedFriendDoc, FirestoreCallBack callBack){
        acceptedFriendDoc.update("friends", FieldValue.arrayUnion(User.getUserDoc().getReference())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                removeFriendRequest(acceptedFriendDoc, callBack);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callBack.onFirestoreResult(false);
            }
        });
    }

    public static void removeFriendRequest(DocumentReference rejectedFriendDoc, FirestoreCallBack callBack){

        User.getUserDoc().getReference().update("friendRequests.received", FieldValue.arrayRemove(rejectedFriendDoc)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                rejectedFriendDoc.update("friendRequests.sent", FieldValue.arrayRemove(User.getUserDoc().getReference()));
                callBack.onFirestoreResult(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callBack.onFirestoreResult(false);
            }
        });
    }

}

