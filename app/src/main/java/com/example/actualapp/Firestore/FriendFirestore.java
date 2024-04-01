package com.example.actualapp.Firestore;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.actualapp.FeedActivity;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendFirestore extends Firestore {

    FriendFirestore(){

    }

    public static void initializeFriendReqListener(){
        db.collection("appUsers").document(User.getUsername()).addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d("FriendFirestore", "Failed to listen.");
                } else {
                    if(value != null && value.exists()){
                        if(value.contains("friendRequests")){
                            List<DocumentReference> friendRequests = (List<DocumentReference>) value.get("friendRequests");
                            if (friendRequests != null){
                                UserFriends.setFriendRequests((ArrayList<DocumentReference>) friendRequests);
                                FeedActivity.updateFriendRequest();
                            }
                            Log.d("FriendFirestore", "listen succeeded");
                        }
                    }
                }
            }
        });
    }

    public static void sendFriendRequest(String id, FirestoreCallBack callBack) {
        db.collection("appUsers").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (QueryDocumentSnapshot document: task.getResult()){
                            document.getReference().update("friendRequests", FieldValue.arrayUnion(User.getUserDoc().getReference())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
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

    public static ArrayList<String> getFriendRequests(){
        return null;
    }

}

