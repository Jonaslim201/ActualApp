package com.example.actualapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.Firestore.FriendFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.example.actualapp.userRelated.UserFriends;

public class FeedActivity extends AppCompatActivity {

    private TextInputEditText id;
    private Button addFriend;
    private static AppCompatTextView friendReq;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_layout);

        addFriend = findViewById(R.id.addFriendButton);
        id = findViewById(R.id.friendId);
        friendReq = findViewById(R.id.newFriendReq);
        updateFriendRequest();

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendFirestore.sendFriendRequest(id.getText().toString(), new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(FeedActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FeedActivity.this, "Id does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public static void updateFriendRequest(){
        if (friendReq != null){
            if(UserFriends.getFriendRequests() != null && !UserFriends.getFriendRequests().isEmpty()){
                for (DocumentReference friendreq:UserFriends.getFriendRequests()){
                    friendreq.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            friendReq.setText(String.valueOf(documentSnapshot.get("username")));
                        }
                    });
                }
            } else {
                friendReq.setText("No friend requests yet");
            }
        }
    }
}
