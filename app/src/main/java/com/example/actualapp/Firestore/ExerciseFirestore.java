package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.actualapp.R;
import com.example.actualapp.activities.ExerciseCategoriesActivity;
import com.example.actualapp.exerciseRelated.Exercise;
import com.example.actualapp.exerciseRelated.ExerciseCallBack;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.userRelated.UserFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
                    exercises.add (new Exercise.ExerciseBuilder().setName(exercise.get(0))
                                                                 .setPriMuscleGroups(exercise.get(1))
                                                                 .setSecMuscleGroups(exercise.get(2))
                                                                 .Build());
                    exerciseCallBack.onSuccessResult(exercises);
                }
            }
        });
    }

    public static void getWorkouts(String category, String exerciseName, WorkoutCallback callback){
        Log.d("ExerciseFirestore", "running getWorkouts");
        Log.d("ExerciseFirestore", exerciseName);
        Log.d("ExerciseFirestore", category);
        List<Workout> workouts = new ArrayList<>();
        UserExercise.getWorkoutsDoc().document(category).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<?> currentWorkouts = (List<?>) documentSnapshot.get(exerciseName);

                if (currentWorkouts != null && !currentWorkouts.isEmpty()){
                    currentWorkouts.remove(0);
                    for (Object workout:currentWorkouts){
                        if (workout instanceof Map){
                            Map<String, Object> mapWorkout = (Map<String, Object>) workout;
                            float weightLifted = ((Number) mapWorkout.get("weightLifted")).floatValue();
                            float numOfReps = ((Number) mapWorkout.get("numOfReps")).floatValue();

                            Log.d("EXERCISEFIRESTORE", mapWorkout.get("dateOfWorkout").toString());

                            workouts.add(new Workout(mapWorkout.get("name").toString(), weightLifted, mapWorkout.get("dateOfWorkout").toString(), (int) numOfReps));;
                        }
                    }

                    callback.onSuccessResult(workouts, false);
                } else {
                    callback.onSuccessResult(null, true);
                }
            }
        });
    }

    public static void insertWorkout(String category, Workout workout, FirestoreCallBack callBack){

        UserExercise.getWorkoutsDoc().document(category).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot currCategoryDoc = task.getResult();
                    List<Object> toAddWorkouts = new ArrayList<>(Collections.singletonList(workout));

                    if (currCategoryDoc.contains(workout.getName())){

                        List<Object> existingWorkouts = (List<Object>) currCategoryDoc.get(workout.getName());
                        Map<String, Object> existingHighScore = (Map<String, Object>) existingWorkouts.get(0);
                        float highScoreWeight = ((Number) existingHighScore.get("weightLifted")).floatValue();
                        float highScoreReps = ((Number) existingHighScore.get("numOfReps")).floatValue();

                        if (workout.getWeightLifted() > highScoreWeight ||
                                (workout.getWeightLifted() == highScoreWeight && workout.getNumOfReps() > highScoreReps)) {
                            existingWorkouts.set(0, workout);
                            addToLeaderboard(category, workout);
                        }

                        existingWorkouts.addAll(toAddWorkouts);

                        UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), existingWorkouts)
                                .addOnSuccessListener(unused -> callBack.onFirestoreResult(true))
                                .addOnFailureListener(e -> callBack.onFirestoreResult(false));

                    } else {
                        toAddWorkouts.add(0,workout);
                        UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), toAddWorkouts)
                                .addOnSuccessListener(unused -> callBack.onFirestoreResult(true))
                                .addOnFailureListener(e -> callBack.onFirestoreResult(false));
                        addToLeaderboard(category, workout);
                    }


                }
            }
        });

    }

    public static void addToLeaderboard(String category, Workout workout){

        String fieldpath = workout.getName() + "." + User.getUsername();
        Map<String, Object> newEntry = new HashMap<>();
        newEntry.put("id", User.getId());
        newEntry.put("username", User.getUsername());

        newEntry.put("dateOfWorkout", workout.getDateOfWorkout());
        newEntry.put("weightLifted", workout.getWeightLifted());
        newEntry.put("numOfReps", workout.getNumOfReps());

        Leaderboard.getLeaderboard().document(category).update(fieldpath, newEntry);
        for (Map.Entry<String, DocumentSnapshot> friends:UserFriends.getFriends().entrySet()){
            friends.getValue().getReference().collection("leaderboards").document(category).update(fieldpath, newEntry);
        }

    }

    public static void addedFriendLeaderboard(DocumentReference newFriendDoc, String acceptedId){

        List<CollectionReference> collectionReferences = new ArrayList<>();
        collectionReferences.add(newFriendDoc.collection("workouts"));
        collectionReferences.add(UserExercise.getWorkoutsDoc());

        for (CollectionReference collection:collectionReferences) {
            for (String category : ExerciseCategoriesActivity.getExerciseArray()) {
                DocumentReference leaderboard = Leaderboard.getLeaderboard().document(category);
                DocumentReference friendLeaderboard = newFriendDoc.collection("leaderboards").document(category);
                collection.document(category).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            for (Map.Entry<String, Object> exercise : documentSnapshot.getData().entrySet()) {
                                if (exercise.getValue() instanceof List) {
                                    List<?> currExercise = (List<?>) exercise.getValue();
                                    Map<String, Object> bestWorkout = (Map<String, Object>) currExercise.get(0);

                                    Log.d("ExerciseFirestore", bestWorkout.toString());



                                    if (collection.getPath().equals(UserExercise.getWorkoutsDoc().getPath())) {
                                        String nameOfEntry = bestWorkout.get("name").toString() + "." + User.getId();
                                        bestWorkout.put("id", User.getId());
                                        bestWorkout.put("username", User.getUsername());
                                        friendLeaderboard.update(nameOfEntry, bestWorkout);
                                    } else {
                                        String nameOfEntry = bestWorkout.get("name").toString() + "." + acceptedId;
                                        bestWorkout.put("id", acceptedId);
                                        final String[] friendsusername = new String[1];
                                        newFriendDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                friendsusername[0] = (String) documentSnapshot.get("username");
                                            }
                                        });
                                        bestWorkout.put("username", friendsusername[0]);
                                        leaderboard.update(nameOfEntry, bestWorkout);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public static void getLeaderboard(String exerciseName, String category, WorkoutCallback callback){

        ArrayList<FriendWorkout> leaderboardList = new ArrayList<>();
        Leaderboard.getLeaderboard().document(category).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Map<String, Object> mapOfWorkouts = (Map<String, Object>) documentSnapshot.get(exerciseName);
                    if (mapOfWorkouts != null){
                        for (Map.Entry<String,Object> workout:mapOfWorkouts.entrySet()){
                            Map<String, Object> currentWorkout = (Map<String, Object>) workout.getValue();
                            Log.d("ExerciseFirestore", currentWorkout.toString());

                            float weightLifted = ((Number) currentWorkout.get("weightLifted")).floatValue();
                            String dateOfWorkout = currentWorkout.get("dateOfWorkout").toString();
                            int numOfReps = Integer.valueOf(currentWorkout.get("numOfReps").toString());
                            String username = currentWorkout.get("username").toString();
                            String id = currentWorkout.get("id").toString();

                            leaderboardList.add(new FriendWorkout(exerciseName, weightLifted, dateOfWorkout,numOfReps,id, username, R.drawable.baseline_person_24));

                        }
                    }
                    if (!leaderboardList.isEmpty()){
                        Log.d("ExerciseFirestore",leaderboardList.toString());
                        Collections.sort(leaderboardList, Comparator.comparingDouble(FriendWorkout::getWeightLifted).reversed());
                        Log.d("ExerciseFirestore",leaderboardList.toString());
                        callback.onSuccessResult(leaderboardList, false);;
                    } else {
                        Log.d("ExerciseFirestore","empty");
                        callback.onSuccessResult(null, true);
                    }

                } else {
                    Log.d("ExerciseFirestore","not found");
                    callback.onSuccessResult(null, true);
                }
            }
        });
    }
}
