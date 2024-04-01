package com.example.actualapp.userRelated;

import com.example.actualapp.exerciseRelated.Workout;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class UserExercise extends User {
    private static CollectionReference workoutsDoc;
    private static CollectionReference leaderboardDoc;

    //ArrayList of the user's workouts
    private static ArrayList<Workout> workouts;

    public static void setLeaderboardDoc(CollectionReference leaderboardDoc) {
        UserExercise.leaderboardDoc = leaderboardDoc;
    }

    public static CollectionReference getLeaderboardDoc() {
        return leaderboardDoc;
    }

    public static void setWorkoutsDoc(CollectionReference workoutsDoc) {
        UserExercise.workoutsDoc = workoutsDoc;
    }

    public static CollectionReference getWorkoutsDoc() {
        return workoutsDoc;
    }

    public static void addWorkout(Workout e) {
        UserExercise.workouts.add(e);
    }

    public static void setWorkouts(ArrayList<Workout> workouts) {
        UserExercise.workouts = workouts;
    }

    public static ArrayList<Workout> getWorkouts() {
        return workouts;
    }

}
