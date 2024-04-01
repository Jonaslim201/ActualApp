package com.example.actualapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.textfield.TextInputEditText;

public class FeedActivity extends AppCompatActivity {

    private TextInputEditText id;
    private Button addFriend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_layout);

        addFriend = findViewById(R.id.addFriendButton);
        id = findViewById(R.id.friendId);

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
}
