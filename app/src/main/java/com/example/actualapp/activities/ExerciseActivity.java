package com.example.actualapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.actualapp.FeedActivity;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.Firestore.FirestoreListener;
import com.example.actualapp.Firestore.LeaderboardChangeListener;
import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.example.actualapp.fragments.LeaderboardFragment;
import com.example.actualapp.fragments.WorkoutFragment;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.UserExercise;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity implements WorkoutFragment.InitializationListener, LeaderboardChangeListener {

    public static boolean isActive = false;
    public static String exerciseName;
    public static String category;
    private WorkoutKey key;
    ArrayList<Workout> workoutRecords;
    private ArrayList<FriendWorkout> leaderboardWorkouts;
    private WorkoutFragment workoutFragment;
    private LeaderboardFragment leaderboardFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        category = names.getString("category");
        key = new WorkoutKey(category, exerciseName);
        Log.d("EXERCISEACTIVITY", exerciseName);
        Log.d("EXERCISEACTIVITY", category);

        workoutRecords = UserExercise.getWorkouts(key);
        workoutFragment = WorkoutFragment.newInstance(exerciseName, category, workoutRecords);

        leaderboardWorkouts = Leaderboard.getLeaderboard(key);
        leaderboardFragment = LeaderboardFragment.newInstance(exerciseName, category, leaderboardWorkouts);
        createView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        workoutFragment.removeInitializationListener();
        leaderboardFragment = null;
        workoutFragment = null;
        FirestoreListener.unregisterLeaderboardListener(this);
        isActive = false;
    }

    public void getLeaderboard(){
        Log.d("EXERCISEACTIVITY", "GETTING LEADERBOARD");
    }

    public void createView(){
        FirestoreListener.registerLeaderboardListener(this);
        setContentView(R.layout.individual_exercise);

        workoutFragment.setInitializationListener(this);

        initialiseButtons();
        SwitchMaterial leaderboardSwitch = findViewById(R.id.leaderboardSwitch);
        leaderboardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (isChecked) {
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.fragmentContainer, leaderboardFragment);
                } else {
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    transaction.replace(R.id.fragmentContainer, workoutFragment);
                }
                transaction.addToBackStack(null).commit();
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, workoutFragment).addToBackStack(null).commit();
        isActive = true;
    }


    public void initialiseButtons(){
        ImageButton backButton = findViewById(R.id.indivExerciseBackButton);
        ImageButton profileButton = findViewById(R.id.indivExerciseProfileButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(ExerciseActivity.this, CategoryActivity.class);
                newActivity.putExtra("category", category);
                startActivity(newActivity);
                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileButton.setBackground(getDrawable(R.drawable.circular_button));
                startActivity(new Intent(ExerciseActivity.this, FeedActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

        ExtendedFloatingActionButton addWorkout = findViewById(R.id.addExerciseRecord);
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked(v);
            }
        });
    }


    public void showAlertDialogButtonClicked(View view){
        //Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Exercise");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.add_workout_layout, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                TextInputEditText reps = customLayout.findViewById(R.id.numOfReps);
                TextInputEditText weight = customLayout.findViewById(R.id.weightLifted);
                int repsStr = Integer.parseInt(reps.getText().toString());
                float weightStr = Float.parseFloat(weight.getText().toString());
                addWorkout(repsStr, weightStr);
            }
        });
        builder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addWorkout(int numOfReps, float weightLifted){
        //Get date + time String value and concatenate them together
        Calendar cal = Calendar.getInstance(new Locale("SG"));
        Date dateNow = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String dateTime = format1.format(dateNow);

        //Creates a new Workout instance
        Workout workout = new Workout(exerciseName,weightLifted, dateTime, numOfReps);

        UserExercise.addWorkout(key, workout, new FirestoreCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success){
                    workoutFragment.addWorkout(workout);
                    Toast.makeText(ExerciseActivity.this, "Workout added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExerciseActivity.this, "Failed to add workout.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onInitializationComplete() {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
    }

    @Override
    public void onLeaderboardChanged(WorkoutKey key) {
        Log.d("EXERCISEACTIVITY", key.toString());
        getLeaderboard();
        ArrayList<FriendWorkout> newRecords = Leaderboard.getLeaderboard(key);
        if (leaderboardFragment.getView() == null){
            leaderboardFragment = LeaderboardFragment.newInstance(exerciseName, category, newRecords);
        } else {
            leaderboardFragment.addWorkout(newRecords);
        }
    }
}