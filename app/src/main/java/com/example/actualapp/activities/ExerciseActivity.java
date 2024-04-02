package com.example.actualapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.LeaderboardActivity;
import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.Workout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity {

    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    String category;
    String exerciseName;
    TextInputEditText dateTextEdit;
    TextInputEditText timeTextEdit;
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


        //This is the setup for the popup calendar
        date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateDateLabel();
            }
        };


        //This is the setup for the time picker
        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                updateTimeLabel();
            }
        };

        //Initialize all the textedit fields
        dateTextEdit = findViewById(R.id.workoutDate);
        numOfReps = findViewById(R.id.numOfReps);
        weightLifted = findViewById(R.id.weightLifted);
        timeTextEdit = findViewById(R.id.workoutTime);

        //Initialize add workout button
        addWorkout = findViewById(R.id.addWorkout);

        //One textview for the exercise name testing
        TextView name = findViewById(R.id.indivExerciseName);
        name.setText(exerciseName);
        Toast.makeText(ExerciseActivity.this, category, Toast.LENGTH_SHORT).show();
        //One textview for the exercise name testing

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


    }

    private void setOnClickListener(){

        //Sets the onClickListener so that the Calender will popup
        dateTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ExerciseActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //Sets the onClickListener so that the Clock will popup
        timeTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ExerciseActivity.this, time, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });


        //onClickListener for adding workout
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get weight value entered in float
                float weight = Float.parseFloat(weightLifted.getText().toString());

                //Get reps entered in integer
                int reps = Integer.parseInt(numOfReps.getText().toString());

                //Get date + time String value and concatenate them together
                String dateTime = dateTextEdit.getText().toString() + " " + timeTextEdit.getText().toString();

                //Creates a new Workout instance
                Workout workout = new Workout(exerciseName,weight, dateTime, reps);

                //Inserts the new Workout instance through Firestore
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

    private void updateDateLabel(){
        String dateFormat="MM/dd/yy";
        SimpleDateFormat newDateFormat=new SimpleDateFormat(dateFormat, Locale.getDefault());
        dateTextEdit.setText(newDateFormat.format(calendar.getTime()));
    }

    private void updateTimeLabel(){
        String timeFormat = "HH:mm";
        SimpleDateFormat newTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());
        timeTextEdit.setText(newTimeFormat.format(calendar.getTime()));
    }
}
