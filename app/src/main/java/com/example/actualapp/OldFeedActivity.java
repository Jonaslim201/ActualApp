package com.example.actualapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.Firestore.FriendFirestore;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;

import java.util.Map;
import java.util.Objects;

public class OldFeedActivity extends AppCompatActivity {

    private TextInputEditText id;
    private Button addFriend;
    private static AppCompatTextView friendReq;
    private Button acceptFriend;
    private Button rejectFriend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_layout);

        addFriend = findViewById(R.id.addFriendButton);
        id = findViewById(R.id.friendId);
        friendReq = findViewById(R.id.newFriendReq);
        acceptFriend = findViewById(R.id.acceptButton);
        rejectFriend = findViewById(R.id.rejectButton);

        setTextWatchers();

        updateFriendRequest();

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FriendFirestore.sendFriendRequest(id.getText().toString(), new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(OldFeedActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            id.setText("");
                        } else {
                            Toast.makeText(OldFeedActivity.this, "Id does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        acceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceptedId = friendReq.getText().toString();
                FriendFirestore.acceptFriendRequest(UserFriends.getReceivedFriendRequests().get(acceptedId), new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(OldFeedActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OldFeedActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, acceptedId);
            }
        });

        rejectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rejectedId = friendReq.getText().toString();
                FriendFirestore.removeFriendRequest(UserFriends.getReceivedFriendRequests().get(rejectedId), new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(OldFeedActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OldFeedActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public static void updateFriendRequest(){
        if (friendReq != null){
            if(UserFriends.getReceivedFriendRequests() != null && !UserFriends.getReceivedFriendRequests().isEmpty()){
                for (Map.Entry<String, DocumentReference> entry:UserFriends.getReceivedFriendRequests().entrySet()){
                    friendReq.setText(entry.getKey());
                }
            } else {
                friendReq.setText("No friend requests yet");
            }
        }
    }

    private void setTextWatchers(){
        final TextWatcher commonTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String idText = Objects.requireNonNull(id.getText()).toString().trim();

                boolean isValid = idText.length() > 0;
                boolean addingThemselves = idText.equals(User.getId());
                boolean alreadyAdded = UserFriends.getFriendDocuments().containsKey(idText);
                boolean alreadyRequested = UserFriends.getSentFriendRequests().containsKey(idText);

                Log.d("FeedActivity", UserFriends.getSentFriendRequests().toString());

                if (addingThemselves || alreadyAdded || alreadyRequested) {
                    // Username and password are the same, disable the register button
                    addFriend.setEnabled(false);
                    // Show a message
                    Toast.makeText(OldFeedActivity.this, "Adding yourself", Toast.LENGTH_SHORT).show();
                } else {
                    addFriend.setEnabled(isValid);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        id.addTextChangedListener(commonTextWatcher);
    }
}
