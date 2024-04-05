package com.example.actualapp.userRelated;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.google.firebase.firestore.CollectionReference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard {

    private static CollectionReference leaderboard;
    private List<Workout> workouts;

    public static CollectionReference getLeaderboard() {
        return leaderboard;
    }

    public static void setLeaderboard(CollectionReference leaderboard) {
        Leaderboard.leaderboard = leaderboard;
    }

    public List<Workout> getLeaderboardForView() {
        // Sort workouts based on weight lifted (descending order)
        Collections.sort(workouts, Comparator.comparingDouble(Workout::getWeightLifted).reversed());
        return workouts;
    }
    public static void getLeaderBoardWorkouts(String exerciseName, String category, WorkoutCallback callback){
        ExerciseFirestore.getLeaderboard(exerciseName, category, callback);
    }


}
