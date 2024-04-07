package com.example.actualapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.actualapp.FeedActivity;
import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.example.actualapp.fragments.leaderboardFragment;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.fragments.workoutFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity implements workoutFragment.InitializationListener{

    String exerciseName;
    String category;
    ArrayList<Workout> workoutRecords;
    private ArrayList<FriendWorkout> leaderboardWorkouts;
    private workoutFragment workoutFragment;
    private leaderboardFragment leaderboardFragment;
    private TextView noRecords;
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
                }
                getLeaderboard();
            }
        });
    }

    public void getLeaderboard(){
        Leaderboard.getLeaderBoardWorkouts(exerciseName, category, new WorkoutCallback() {
            @Override
            public void onSuccessResult(List<? extends Workout> workouts, boolean isEmpty) {
                if (!isEmpty){
                    leaderboardWorkouts = (ArrayList<FriendWorkout>) workouts;
                }
                createView();
            }
        });
    }

    public void createView(){
        setContentView(R.layout.individual_exercise);
        noRecords = findViewById(R.id.noRecords);

        workoutFragment = new workoutFragment(exerciseName, category, workoutRecords);
        workoutFragment.setInitializationListener(this);

        leaderboardFragment = new leaderboardFragment(exerciseName, category, leaderboardWorkouts);
        initialiseButtons();
        SwitchMaterial leaderboardSwitch = findViewById(R.id.leaderboardSwitch);
        leaderboardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (isChecked) {
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.fragmentContainer, leaderboardFragment);
                    checkArrays(workoutRecords);
                } else {
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    transaction.replace(R.id.fragmentContainer, workoutFragment);
                    checkArrays(leaderboardWorkouts);
                }
                transaction.addToBackStack(null).commit();
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, workoutFragment).addToBackStack(null).commit();
//        transaction.addToBackStack(null); // Add transaction to the back stack
//        transaction.commit();
    }

    public void checkArrays(List<?> list){
        if (list == null || list.isEmpty()){
            noRecords.setVisibility(View.VISIBLE);
        } else {
            noRecords.setVisibility(View.GONE);
        }
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



    @Override
    public void onInitializationComplete() {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
    }
}