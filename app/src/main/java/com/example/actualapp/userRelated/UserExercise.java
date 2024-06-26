package com.example.actualapp.userRelated;

import android.util.Log;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserExercise extends User {
    private static CollectionReference workoutsDoc;

    //ArrayList of the user's workouts
    private static HashMap<WorkoutKey, List<Workout>> workoutMap;

    public static void setWorkoutsDoc(CollectionReference workoutsDoc) {
        UserExercise.workoutsDoc = workoutsDoc;
    }

    public static CollectionReference getWorkoutsDoc() {
        return workoutsDoc;
    }

    public static void setWorkoutMap(HashMap<WorkoutKey, List<Workout>> workoutMap) {
        UserExercise.workoutMap = workoutMap;
    }

    public static ArrayList<Workout> getWorkouts(WorkoutKey key) {
        if(workoutMap == null) {
            workoutMap = new HashMap<>();
            return null;
        }

        if (workoutMap.get(key) == null) {
            return null;
        } else {
            return (ArrayList<Workout>) workoutMap.get(key);
        }
    }

    //Inserts the new Workout instance through Firestore
    public static void addWorkout(WorkoutKey key, Workout workout, FirestoreCallBack firestoreCallBack) {
        boolean workoutExists = false;
        Log.d("UserExercise", "addWorkout:" + workout.getName() + " " + workout.getWeightLifted() + " " + workout.getDateOfWorkout() + " " + workout.getNumOfReps() + " " + key.getCategory() + " " + key.getExerciseName());
        List<Workout> workoutList = workoutMap.get(key);
        if (workoutList == null) {
            workoutList = new ArrayList<>();
        } else {
            String dateOfNewWorkout = workout.getDateOfWorkout();
            workoutExists = workoutList.stream().anyMatch(workout1 -> workout1.getDateOfWorkout().equals(dateOfNewWorkout));
        }

        if (!workoutExists) {
            workoutList.add(workout);
            workoutMap.put(key, workoutList);
            ExerciseFirestore.insertWorkout(key.getCategory(), workout, firestoreCallBack);
        }
    }

}
