package com.example.actualapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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
import java.util.List;
import java.util.Locale;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity implements WorkoutFragment.InitializationListener, LeaderboardChangeListener {

    public static ExerciseActivity instance;
    private ViewPager2 viewPager;

    public String exerciseName;

    public String category;
    private WorkoutKey key;
    ArrayList<Workout> workoutRecords;
    private ArrayList<FriendWorkout> leaderboardWorkouts;
    private WorkoutFragment workoutFragment;
    private LeaderboardFragment leaderboardFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;
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

    private void setupViewPager(ViewPager2 viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(workoutFragment);
        adapter.addFragment(leaderboardFragment);
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        workoutFragment.removeInitializationListener();
        leaderboardFragment = null;
        workoutFragment = null;
        FirestoreListener.unregisterLeaderboardListener(this);
    }

    public void createView(){
        setContentView(R.layout.individual_exercise);

        viewPager = findViewById(R.id.viewPager2);
        setupViewPager(viewPager);

        viewPager.setOffscreenPageLimit(1);

        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                if (position < -1) {
                    // This page is way off-screen to the left.
                    page.setAlpha(0);
                } else if (position <= 1) {
                    // Modify the default slide transition to shrink the page as well
                    page.setAlpha(1);
                    page.setTranslationX(0);
                    page.setScaleX(1);
                    page.setScaleY(1);
                } else {
                    // This page is way off-screen to the right.
                    page.setAlpha(0);
                }
            }
        });

        workoutFragment.setInitializationListener(this);

        initialiseButtons();

        SwitchMaterial leaderboardSwitch = findViewById(R.id.leaderboardSwitch);
        leaderboardSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (isChecked) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
            transaction.addToBackStack(null).commit();
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                SwitchMaterial leaderboardSwitch = findViewById(R.id.leaderboardSwitch);
                leaderboardSwitch.setChecked(position == 1);
            }
        });
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
                Log.d("ExerciseActivity", "onClick: Profile Button Clicked");
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
//        Workout workout = new Workout(category, exerciseName, weightLifted, dateTime, numOfReps);
        Workout workout = new Workout.WorkoutBuilder().setCategory(category)
                .setName(exerciseName)
                .setWeightLifted(weightLifted)
                .setDateOfWorkout(dateTime)
                .setNumOfReps(numOfReps)
                .Build();
        UserExercise.addWorkout(key, workout, new FirestoreCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success){
                    workoutFragment.addWorkout(workout, key);
                    Toast.makeText(ExerciseActivity.this, "Workout added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExerciseActivity.this, "Failed to add workout.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onInitializationComplete() {
        FirestoreListener.registerLeaderboardListener(this);
    }

    public boolean isInitialized() {
        return viewPager != null && workoutFragment != null && leaderboardFragment != null;
    }

    @Override
    public void onLeaderboardChanged(WorkoutKey key) {
        Log.d("EXERCISEACTIVITY", key.toString());
        ArrayList<FriendWorkout> newRecords = Leaderboard.getLeaderboard(key);
        Log.d("EXERCISEACTIVITY", newRecords.toString());
        if (leaderboardFragment.getView() == null){
            Log.d("EXERCISEACTIVITY", "null");
            leaderboardFragment.setLeaderboardWorkouts(newRecords);
        } else {
            leaderboardFragment.addWorkout(newRecords);
        }
    }
}