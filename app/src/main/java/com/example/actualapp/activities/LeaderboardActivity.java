package com.example.actualapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.example.actualapp.recyclerAdapters.LeaderboardAdapter;

import java.util.List;


//Leaderboard testing
public class LeaderboardActivity extends AppCompatActivity {

    private String exerciseName;
    private String category;
    private List<FriendWorkout> workouts;

    private RecyclerView recyclerViewLeaderboard;
    private LeaderboardAdapter leaderboardAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_layout);

        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        category = names.getString("category");
        Leaderboard.getLeaderBoardWorkouts(exerciseName, category, new WorkoutCallback() {
                    @Override
                    public void onSuccessResult(List<FriendWorkout> workouts, boolean isEmpty) {
                        if (!isEmpty){
                            setWorkouts(workouts);
                            Log.d("Leaderboard", "workouts found");
                            Log.d("Leaderboard", workouts.toString());
                            setView();
                        } else {
                            Log.d("Leaderboard", "workouts not found");
                            setAnotherView();
                        }
                    }
                });

    }

    public void setWorkouts(List<FriendWorkout> workouts) {
        this.workouts = workouts;
    }

    public void setView(){
        Log.d("Leaderboard", "setView running");
        recyclerViewLeaderboard = findViewById(R.id.leaderboardRecyclerView);
        leaderboardAdapter = new LeaderboardAdapter(workouts);
        recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
        recyclerViewLeaderboard.setAdapter(leaderboardAdapter);
        Log.d("Leaderboard", "adapter set");

        Toolbar toolbar = findViewById(R.id.leaderboardExerciseName);
        setSupportActionBar(toolbar);

        Log.d("Leaderboard", exerciseName);
        getSupportActionBar().setTitle(exerciseName);

    }

    public void setAnotherView(){
        setContentView(R.layout.leaderboard_layout);
        Toolbar toolbar = findViewById(R.id.leaderboardExerciseName);
        setSupportActionBar(toolbar);

        Log.d("Leaderboard", exerciseName);
        getSupportActionBar().setTitle(exerciseName);
    }
}
