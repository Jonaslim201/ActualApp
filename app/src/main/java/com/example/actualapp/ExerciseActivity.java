package com.example.actualapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting the exercise name the user has chosen from the ExerciseCategoriesActivity class
        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exerciseName");
        setContentView(R.layout.indiv_exercise_temp_layout);

        //One textview for the exercise name testing
        TextView name = findViewById(R.id.indivExerciseName);
        name.setText(exerciseName);

        //just one button for my Leaderboard testing
        Button leaderboardButton = findViewById(R.id.leaderboardButton);
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, LeaderboardActivity.class);
                intent.putExtra("exerciseName", exerciseName);
                startActivity(intent);
            }
        });
    }
}
