package com.example.actualapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.recyclerAdapters.LeaderboardAdapter;

import java.util.List;

public class leaderboardFragment extends Fragment {

    private View leaderboardView;
    private AppCompatActivity activity;

    private String exerciseName;
    private String category;
    private List<FriendWorkout> leaderboardWorkouts;

    private RecyclerView recyclerViewLeaderboard;
    private LeaderboardAdapter leaderboardAdapter;

    public leaderboardFragment(String exerciseName, String category, List<FriendWorkout> leaderboardWorkouts){
        this.exerciseName = exerciseName;
        this.category = category;
        this.leaderboardWorkouts = leaderboardWorkouts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        leaderboardView =  inflater.inflate(R.layout.leaderboard_layout, container, false);
        activity = (AppCompatActivity) getActivity();
        if (leaderboardWorkouts == null){
            setAnotherView();
        } else if (leaderboardWorkouts != null){
            setView();
        }
        return leaderboardView;
    }

    public void setLeaderboardWorkouts(List<FriendWorkout> leaderboardWorkouts) {
        this.leaderboardWorkouts = leaderboardWorkouts;
    }

    public void setView(){
        Log.d("Leaderboard", "setView running");
        recyclerViewLeaderboard = leaderboardView.findViewById(R.id.leaderboardRecyclerView);
        leaderboardAdapter = new LeaderboardAdapter(leaderboardWorkouts);
        recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(leaderboardView.getContext()));
        recyclerViewLeaderboard.setAdapter(leaderboardAdapter);
        Log.d("Leaderboard", "adapter set");

        if(activity!=null){
            Toolbar toolbar = leaderboardView.findViewById(R.id.leaderboardExerciseName);;
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();

            if(actionBar != null){
                Log.d("Leaderboard", exerciseName);
                actionBar.setTitle(exerciseName);
            }
        }

    }

    public void setAnotherView(){
        if(activity!=null){
            Toolbar toolbar = leaderboardView.findViewById(R.id.leaderboardExerciseName);;
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();

            if(actionBar != null){
                Log.d("Leaderboard", exerciseName);
                actionBar.setTitle(exerciseName);
            }
        }
    }
}
