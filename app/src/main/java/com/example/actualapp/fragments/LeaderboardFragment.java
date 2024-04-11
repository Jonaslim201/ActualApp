package com.example.actualapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.recyclerAdapters.LeaderboardAdapter;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment {

    private View leaderboardView;
    private AppCompatActivity activity;

    private String exerciseName;
    private String category;
    private ArrayList<FriendWorkout> leaderboardWorkouts;
    private LeaderboardAdapter leaderboardAdapter;
    private static Bundle args;


    public static LeaderboardFragment newInstance(String exerciseName, String category, ArrayList<FriendWorkout> leaderboardRecords){
        Log.d("LEADERBOARD", "INITIALISING INSTANCE");
        LeaderboardFragment fragment = new LeaderboardFragment();
        args = new Bundle();
        args.putString("exerciseName", exerciseName);
        args.putString("category", category);
        args.putParcelableArrayList("leaderboardRecords", leaderboardRecords);
        fragment.setArguments(args);
        return fragment;
    }

    public LeaderboardFragment(){
    }

    @Override
    public void onResume() {
        super.onResume();
        if (leaderboardAdapter != null){
            leaderboardAdapter.notifyDataSetChanged();
        }

    }

    public void setLeaderboardWorkouts(ArrayList<FriendWorkout> leaderboardWorkouts) {
        args.remove("leaderboardRecords");
        args.putParcelableArrayList("leaderboardRecords", leaderboardWorkouts);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        leaderboardView =  inflater.inflate(R.layout.leaderboard_layout, container, false);
        Log.d("LEADERBOARD", "CREATING VIEW");

        Bundle args = getArguments();
        if (args != null){
            exerciseName = args.getString("exerciseName");
            category = args.getString("category");
            leaderboardWorkouts = args.getParcelableArrayList("leaderboardRecords");
        }

        activity = (AppCompatActivity) getActivity();
        if(leaderboardWorkouts != null && !leaderboardWorkouts.isEmpty()){
            recordsListInit();
        } else {
            leaderboardView.findViewById(R.id.noRecords).setVisibility(View.VISIBLE);
        }

//        setView();
        return leaderboardView;
    }

    public void recordsListInit(){
        RecyclerView recyclerViewLeaderboard = leaderboardView.findViewById(R.id.leaderboardRecyclerView);
        leaderboardAdapter = new LeaderboardAdapter(leaderboardView.getContext(), leaderboardWorkouts);
        recyclerViewLeaderboard.setAdapter(leaderboardAdapter);
        recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(leaderboardView.getContext()));
    }

//    public void setView(){
//        if(activity!=null){
//            Toolbar toolbar = leaderboardView.findViewById(R.id.leaderboardExerciseName);;
//            activity.setSupportActionBar(toolbar);
//            ActionBar actionBar = activity.getSupportActionBar();
//
//            if(actionBar != null){
//                actionBar.setTitle(exerciseName);
//            }
//        }
//    }

    public void addWorkout(ArrayList<FriendWorkout> workouts) {
        Log.d("LEADERBOARD", workouts.toString());
        if (leaderboardWorkouts != null){
            leaderboardWorkouts.clear();
            leaderboardWorkouts.addAll(workouts);
        } else {
            leaderboardWorkouts = workouts;
        }

        if (leaderboardAdapter == null){
            recordsListInit();
        } else {
            updateWorkouts(workouts);
        }

    }

    private void updateWorkouts(ArrayList<FriendWorkout> workouts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return leaderboardWorkouts.size();
            }

            @Override
            public int getNewListSize() {
                return workouts.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                FriendWorkout oldItem = leaderboardWorkouts.get(oldItemPosition);
                FriendWorkout newItem = workouts.get(newItemPosition);
                return newItem.getId().equals(oldItem.getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                FriendWorkout oldItem = leaderboardWorkouts.get(oldItemPosition);
                FriendWorkout newItem = workouts.get(newItemPosition);

                return newItem.getWeightLifted() == oldItem.getWeightLifted() && newItem.getNumOfReps() == oldItem.getNumOfReps();
            }
        });
        Log.d("LEADERBOARD", workouts.toString());
        leaderboardAdapter.setLeaderboard(workouts);
        diffResult.dispatchUpdatesTo(leaderboardAdapter);
        leaderboardAdapter.notifyDataSetChanged();
    }

}
