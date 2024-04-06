package com.example.actualapp.userRelated;

import android.util.Log;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserExercise extends User {
    private static CollectionReference workoutsDoc;

    //ArrayList of the user's workouts
    private static ArrayList<Workout> workouts;

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

    public static void getWorkouts(String category, String exerciseName, WorkoutCallback callback) {
        Log.d("UserExercise", "running getWorkouts");
        ExerciseFirestore.getWorkouts(category, exerciseName, new WorkoutCallback() {
            @Override
            public void onSuccessResult(List<? extends Workout> workouts, boolean isEmpty) {
                Log.d("UserExercise", "getWorkouts ran");
                if (!isEmpty){
                    Collections.sort(workouts);
                    callback.onSuccessResult(workouts, false);
                } else {
                    callback.onSuccessResult(null, true);
                }
            }
        });
    }

}
