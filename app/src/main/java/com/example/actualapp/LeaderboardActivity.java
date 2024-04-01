package com.example.actualapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


//Leaderboard testing
public class LeaderboardActivity extends AppCompatActivity {

    private String exerciseName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        String category = names.getString("category");
        setView();

    }

    public void setView(){
        setContentView(R.layout.leaderboard_layout);
        Toolbar toolbar = findViewById(R.id.leaderboardExerciseName);
        setSupportActionBar(toolbar);

        Log.d("Leaderboard", exerciseName);
        getSupportActionBar().setTitle(exerciseName);

    }
}
