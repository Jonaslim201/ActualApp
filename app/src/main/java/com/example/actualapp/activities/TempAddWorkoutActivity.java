package com.example.actualapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.Workout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TempAddWorkoutActivity extends AppCompatActivity {
    String category;
    String exerciseName;
    TextInputEditText numOfReps;
    TextInputEditText weightLifted;
    Button addWorkout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting the exercise name and category the user has chosen from the ExerciseCategoriesActivity class
        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        category = names.getString("category");
        setContentView(R.layout.indiv_exercise_temp_layout);


        //Initialize all the textedit fields
        numOfReps = findViewById(R.id.numOfReps);
        weightLifted = findViewById(R.id.weightLifted);


        //Initialize add workout button
        addWorkout = findViewById(R.id.addWorkout);

        //just one button for my Leaderboard testing
        Button leaderboardButton = findViewById(R.id.leaderboardButton);
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TempAddWorkoutActivity.this, LeaderboardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("exerciseName", exerciseName);
                bundle.putString("category", category);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setOnClickListeners();

    }

    private void setOnClickListeners(){


        //onClickListener for adding workout
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get weight value entered in float
                float weight = Float.parseFloat(weightLifted.getText().toString());

                //Get reps entered in integer
                int reps = Integer.parseInt(numOfReps.getText().toString());

                //Get date + time String value and concatenate them together
                Calendar cal = Calendar.getInstance(new Locale("SG"));
                Date dateNow = cal.getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String dateTime = format1.format(dateNow);

                //Creates a new Workout instance
                Workout workout = new Workout(exerciseName,weight, dateTime, reps);

                //Inserts the new Workout instance through Firestore
                ExerciseFirestore.insertWorkout(category, workout, new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(TempAddWorkoutActivity.this, "succeded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TempAddWorkoutActivity.this, "not succeded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
