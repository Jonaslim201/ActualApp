package com.example.actualapp.userRelated;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.google.firebase.firestore.CollectionReference;

public class Leaderboard {

    private static CollectionReference leaderboard;

    public static CollectionReference getLeaderboard() {
        return leaderboard;
    }

    public static void setLeaderboard(CollectionReference leaderboard) {
        Leaderboard.leaderboard = leaderboard;
    }

    public static void getLeaderBoardWorkouts(String exerciseName, String category, WorkoutCallback callback){
        ExerciseFirestore.getLeaderboard(exerciseName, category, callback);
    }


}
