package com.example.actualapp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendFirestore extends Firestore {

    FriendFirestore(){

    }

    public static void sendFriendRequest(String id, FirestoreCallBack callBack) {
        Firestore.getDb().collection("appUsers").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (QueryDocumentSnapshot document: task.getResult()){
                            document.getReference().update("friendRequests", FieldValue.arrayUnion(User.getUsername())).addOnSuccessListener(new OnSuccessListener<Void>() {
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

}

