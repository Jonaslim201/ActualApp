package com.example.actualapp;

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
        setView();

    }

    public void setView(){
        setContentView(R.layout.leaderboard_layout);
        exerciseName = getIntent().getStringExtra("exerciseName");
        Toolbar toolbar = findViewById(R.id.leaderboardExerciseName);
        setSupportActionBar(toolbar);

        Log.d("Leaderboard", exerciseName);
        getSupportActionBar().setTitle(exerciseName);

    }
}
