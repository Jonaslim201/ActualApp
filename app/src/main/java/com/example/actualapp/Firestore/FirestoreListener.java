package com.example.actualapp.Firestore;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//ADD THESE METHODS TO THE HOMEPAGE
public class FirestoreListener extends Firestore{

    private static FirestoreListener instanceListener;
    private final List<ListenerRegistration> listeners = new ArrayList<>();

    private FirestoreListener(){
    }

    public static FirestoreListener getInstanceListener() {
        if (instanceListener == null){
            instanceListener = new FirestoreListener();
        }
        return instanceListener;
    }

    public void friendReqListener(){
        if (instanceListener != null){
            listeners.add(db.collection("appUsers").document(User.getUsername()).addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot amendedValue, @Nullable FirebaseFirestoreException error) {
                    if (error != null){
                        Log.d("FriendFirestore", "Failed to listen.");
                    } else {
                        if(amendedValue != null && amendedValue.exists()){
                            if(amendedValue.contains("friendRequests")){
                                Map<String, Object> friendRequests = (Map<String, Object>) amendedValue.get("friendRequests");

                                Log.d("FriendFirestore", friendRequests.toString());
                                if (friendRequests != null){
                                    UserFriends.setReceivedFriendRequests((ArrayList<DocumentReference>) friendRequests.get("received"));
                                    UserFriends.setSentFriendRequests((ArrayList<DocumentReference>) friendRequests.get("sent"));
                                }
                                Log.d("FriendFirestore", "listen succeeded");
                            }
                        }
                    }
                }
            }));
        }
    }

    public void friendsListener(){
        if (instanceListener != null){
            listeners.add(db.collection("appUsers").document(User.getUsername()).addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot amendedValue, @Nullable FirebaseFirestoreException error) {
                    if (error != null){
                        Log.d("FriendFirestore", "Failed to listen.");
                    } else {
                        if(amendedValue != null && amendedValue.exists()){
                            if(amendedValue.contains("friendRequests")){
                                List<DocumentReference> friends = (List<DocumentReference>) amendedValue.get("friends");
                                Log.d("FriendFirestore", friends.toString());
                                if (friends != null){
                                    UserFriends.setFriends((ArrayList<DocumentReference>) friends);
                                }
                                Log.d("FriendFirestore", "listen succeeded");
                            }
                        }
                    }
                }
            }));

        }
    }

    public void stopListener(){
        if (!listeners.isEmpty()){
            listeners.clear();
        }
    }
}
