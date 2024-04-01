package com.example.actualapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.exerciseRelated.Workout;
import com.google.android.material.textfield.TextInputEditText;

import com.example.actualapp.userRelated.UserExercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity {

    final Calendar calendar = Calendar.getInstance();
    String category;
    String exerciseName;
    TextInputEditText dateTextEdit;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting the exercise name the user has chosen from the ExerciseCategoriesActivity class
        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        category = names.getString("category");
        setContentView(R.layout.indiv_exercise_temp_layout);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        dateTextEdit = findViewById(R.id.workoutDate);
        TextInputEditText numOfReps = findViewById(R.id.numOfReps);
        TextInputEditText weightLifted = findViewById(R.id.weightLifted);

        ArrayList<Workout> allWorkouts = UserExercise.getWorkouts();
        ArrayList<Workout> currWorkouts = new ArrayList<>();

//        for (Workout workout:allWorkouts){
//            if (workout.getName().equals());
//        }

        //One textview for the exercise name testing
        TextView name = findViewById(R.id.indivExerciseName);
        name.setText(exerciseName);
        Toast.makeText(ExerciseActivity.this, category, Toast.LENGTH_SHORT).show();
        //just one button for my Leaderboard testing
        Button leaderboardButton = findViewById(R.id.leaderboardButton);
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, LeaderboardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("exerciseName", exerciseName);
                bundle.putString("category", category);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        dateTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ExerciseActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button addWorkout = findViewById(R.id.addWorkout);
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float weight = Float.parseFloat(weightLifted.getText().toString());
                int reps = Integer.parseInt(numOfReps.getText().toString());

                Workout workout = new Workout(exerciseName,weight, dateTextEdit.getText().toString(), reps);
                ExerciseFirestore.insertWorkout(category, workout, new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(ExerciseActivity.this, "succeded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ExerciseActivity.this, "not succeded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
        dateTextEdit.setText(dateFormat.format(calendar.getTime()));
    }
}
