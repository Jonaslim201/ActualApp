package com.example.actualapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.example.actualapp.leaderboardFragment;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.workoutFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity implements workoutFragment.InitializationListener{

    String exerciseName;
    String category;
    ArrayList<Workout> workoutRecords;
    private List<FriendWorkout> leaderboardWorkouts;
    private workoutFragment workoutFragment;
    private leaderboardFragment leaderboardFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        category = names.getString("category");

        UserExercise.getWorkouts(category, exerciseName, new WorkoutCallback() {
            @Override
            public void onSuccessResult(List<? extends Workout> workouts, boolean isEmpty) {
                if (!isEmpty){
                    workoutRecords = (ArrayList<Workout>) workouts;
                    getLeaderboard();
                }
            }
        });
    }

    public void getLeaderboard(){
        Leaderboard.getLeaderBoardWorkouts(exerciseName, category, new WorkoutCallback() {
            @Override
            public void onSuccessResult(List<? extends Workout> workouts, boolean isEmpty) {
                if (!isEmpty){
                    leaderboardWorkouts = (List<FriendWorkout>) workouts;
                    Log.d("Leaderboard", "workouts found");
                    Log.d("Leaderboard", workouts.toString());
                    createView();
                } else {
                    Log.d("Leaderboard", "workouts not found");
                    createView();
                }
            }
        });
    }

    public void createView(){
        setContentView(R.layout.individual_exercise);

        Log.d("ExerciseActivityCreateView", workoutRecords.toString());

        workoutFragment = new workoutFragment(exerciseName, category, workoutRecords);
        workoutFragment.setInitializationListener(this);

        leaderboardFragment = new leaderboardFragment(exerciseName, category, leaderboardWorkouts);
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

        ExtendedFloatingActionButton addWorkout = findViewById(R.id.addExerciseRecord);
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myActivity = new Intent(ExerciseActivity.this, TempAddWorkoutActivity.class);
                Bundle intent = new Bundle();
                intent.putString("exerciseName", exerciseName);
                intent.putString("category", category);
                myActivity.putExtras(intent);

                startActivity(myActivity);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, workoutFragment);
        transaction.addToBackStack(null); // Add transaction to the back stack
        transaction.commit();
    }


    @Override
    public void onInitializationComplete() {
        Log.d("IS THIS BEING CALLED", "THIS WAS CALLED");
        getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
    }
}