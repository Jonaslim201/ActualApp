package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.actualapp.exerciseRelated.Exercise;
import com.example.actualapp.exerciseRelated.ExerciseCallBack;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.exerciseRelated.Workout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExerciseFirestore extends Firestore{

    //Getting the list of exercises for a category for the RecycleView
    public static void getExercises(Context activity, String category, ExerciseCallBack exerciseCallBack){
        initializeDatabase(activity);
        ArrayList<Exercise> exercises = new ArrayList<>();
        db.collection("ExerciseCategories").document(category).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                for(Map.Entry<String, Object> entry: map.entrySet()){
                    ArrayList<String> exercise = (ArrayList<String>) entry.getValue();
                    exercises.add(new Exercise(exercise.get(0), exercise.get(1), exercise.get(2)));
                    exerciseCallBack.onSuccessResult(exercises);
                }
            }
        });
    }

    public static void insertWorkout(String category, Workout workout, FirestoreCallBack callBack){

        Log.d("Checking exercise", UserExercise.getWorkoutsDoc().getPath());
        UserExercise.getWorkoutsDoc().document(category).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("Checking exercise", UserExercise.getWorkoutsDoc().document(category).getPath());
                    DocumentSnapshot currCategoryDoc = task.getResult();
                    List<Object> toAddWorkouts = new ArrayList<>(Collections.singletonList(workout));

                    if (currCategoryDoc.contains(workout.getName())){
                        List<Object> existingWorkouts = (List<Object>) currCategoryDoc.get(workout.getName());
                        Number existingHighScore = (Number) existingWorkouts.get(0);
                        float highScore = existingHighScore.floatValue();
                        if (workout.getWeightLifted() >= highScore){
                            highScore = workout.getWeightLifted();
                            existingWorkouts.remove(0);
                            existingWorkouts.add(0, highScore);
                        }
                        existingWorkouts.addAll(toAddWorkouts);
                        UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), existingWorkouts)
                                .addOnSuccessListener(unused -> callBack.onFirestoreResult(true))
                                .addOnFailureListener(e -> callBack.onFirestoreResult(false));

                    } else {
                        toAddWorkouts.add(0,workout.getWeightLifted());
                        UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), toAddWorkouts)
                                .addOnSuccessListener(unused -> callBack.onFirestoreResult(true))
                                .addOnFailureListener(e -> callBack.onFirestoreResult(false));
                    }
                }
            }
        });

    }
}
