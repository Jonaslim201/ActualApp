package com.example.actualapp.Firestore;

import android.content.Context;
import android.util.Log;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.Exercise;
import com.example.actualapp.exerciseRelated.ExerciseCallBack;
import com.example.actualapp.exerciseRelated.ExerciseNamesCallback;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.userRelated.UserFriends;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseFirestore extends Firestore{

    private static final String[] exerciseArray = {
            "Abs", "Back", "Biceps", "Calf", "Chest", "Legs", "Forearms", "Legs", "Shoulders", "Triceps"
    };

    //Getting the list of exercises for a category for the RecycleView
    public static void getExercises(Context activity, String category, ExerciseCallBack exerciseCallBack){
        initializeDatabase(activity);
        ArrayList<Exercise> exercises = new ArrayList<>();

        db.collection("ExerciseCategories").document(category).get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> map = documentSnapshot.getData();
            for(Map.Entry<String, Object> entry: map.entrySet()){
                ArrayList<String> exercise = (ArrayList<String>) entry.getValue();
                exercises.add (new Exercise.ExerciseBuilder().setName(exercise.get(0))
                                                             .setPriMuscleGroups(exercise.get(1))
                                                             .setSecMuscleGroups(exercise.get(2))
                                                             .Build());
                exerciseCallBack.onSuccessResult(exercises);
            }
        });
    }

    //Getting the list of exercises for a category for the RecycleView
    public static void getAllExerciseName(ExerciseNamesCallback callback){
        final HashMap<String, List<String>> exerciseMap = new HashMap<>();
        final int[] remainingCalls = {5}; //exerciseArray.length

        for (String category:exerciseArray){
            List<String> exerciseNames = new ArrayList<>();
            db.collection("ExerciseCategories").document(category).get().addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> map = documentSnapshot.getData();
                if (map != null){
                    for(Map.Entry<String, Object> entry: map.entrySet()){
                        ArrayList<String> exercise = (ArrayList<String>) entry.getValue();
                        exerciseNames.add(exercise.get(0));
                    }
                    exerciseMap.put(category, exerciseNames);

                    synchronized (remainingCalls){
                        remainingCalls[0]--;
                        if (remainingCalls[0] == 0){
                            Log.d("ExerciseFirestore", exerciseMap.toString());
                            callback.onSuccessResult(exerciseMap);
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                synchronized (remainingCalls){
                    remainingCalls[0]--;
                    if (remainingCalls[0] == 0){
                        callback.onSuccessResult(exerciseMap);
                    }
                }
            });
        }
    }

    //Inserts the new workout into the Firestore
    public static void insertWorkout(String category, Workout workout, FirestoreCallBack callBack){

        UserExercise.getWorkoutsDoc().document(category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot currCategoryDoc = task.getResult();
                List<Object> toAddWorkouts = new ArrayList<>(Collections.singletonList(workout));

                if (currCategoryDoc.contains(workout.getName())){

                    List<Object> existingWorkouts = (List<Object>) currCategoryDoc.get(workout.getName());
                    Map<String, Object> existingHighScore = (Map<String, Object>) existingWorkouts.get(0);

                    float highScoreWeight = ((Number) Objects.requireNonNull(existingHighScore.get("weightLifted"))).floatValue();
                    float highScoreReps = ((Number) Objects.requireNonNull(existingHighScore.get("numOfReps"))).floatValue();

                    if (workout.getWeightLifted() > highScoreWeight ||
                            (workout.getWeightLifted() == highScoreWeight && workout.getNumOfReps() > highScoreReps)) {
                        existingWorkouts.add(0, workout);
                        addToLeaderboard(category, workout);
                    } else {
                        existingWorkouts.add(workout);
                    }

                    UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), existingWorkouts).addOnSuccessListener(unused -> {
                                callBack.onFirestoreResult(true);
                                addToFeed(workout);
                            }).addOnFailureListener(e -> callBack.onFirestoreResult(false));

                } else {
                    toAddWorkouts.add(0,workout);
                    UserExercise.getWorkoutsDoc().document(category).update(workout.getName(), toAddWorkouts).addOnSuccessListener(unused -> {
                        addToLeaderboard(category, workout);
                        callBack.onFirestoreResult(true);
                        addToFeed(workout);
                    }).addOnFailureListener(e -> callBack.onFirestoreResult(false));
                }
            } else {
                Log.d("ExerciseFirestore", "Failed to get document.");
            }
        });

    }

    public static void addToFeed(Workout workout){
        FriendWorkout newWorkout = new FriendWorkout(workout, User.getId(), User.getUsername(), R.drawable.baseline_person_24);
        Log.d("ExerciseFirestore", newWorkout.getCategory());
        for (Map.Entry<String, DocumentSnapshot> friends:UserFriends.getFriendDocuments().entrySet()){
            friends.getValue().getReference().collection("feed").document("feed").update("actualFeed", FieldValue.arrayUnion(newWorkout));
        }
    }

    //Adds the new workout to the leaderboard
    public static void addToLeaderboard(String category, Workout workout){

        String fieldpath = workout.getName() + "." + User.getUsername();
        FriendWorkout newPR = new FriendWorkout(workout, User.getId(), User.getUsername(), R.drawable.baseline_person_24);
        Log.d("ExerciseFirestore", newPR.getCategory());
        Leaderboard.getLeaderboardCollection().document(category).update(fieldpath, newPR);
        for (Map.Entry<String, DocumentSnapshot> friends:UserFriends.getFriendDocuments().entrySet()){
            friends.getValue().getReference().collection("leaderboards").document(category).update(fieldpath, newPR);
        }

    }

    //Updates the leaderboard when a new friend is added
    public static void addedFriendLeaderboard(DocumentReference newFriendDoc, String acceptedId){

        //List of the collection references to update,
        // the first one is the new friend's workout collection,
        // the second is the user's workout collection
        List<CollectionReference> collectionReferences = new ArrayList<>();
        collectionReferences.add(newFriendDoc.collection("workouts"));
        collectionReferences.add(UserExercise.getWorkoutsDoc());

        CollectionReference userLeaderboardCollection = Leaderboard.getLeaderboardCollection();
        CollectionReference friendLeaderboardCollection = newFriendDoc.collection("leaderboards");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        for (CollectionReference collection:collectionReferences) {
            Log.d("ExerciseFirestore", collection.getPath());
            executorService.execute(() -> {
                for (String category : exerciseArray) {
                    DocumentReference leaderboard = userLeaderboardCollection.document(category);
                    DocumentReference friendLeaderboard = friendLeaderboardCollection.document(category);

                    //Get the best workout for each exercise
                    collection.document(category).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            for (Map.Entry<String, Object> exercise : Objects.requireNonNull(documentSnapshot.getData()).entrySet()) {
                                if (exercise.getValue() instanceof List) {
                                    List<?> currExercise = (List<?>) exercise.getValue();
                                    Map<String, Object> bestWorkout = (Map<String, Object>) currExercise.get(0);

                                    Log.d("ExerciseFirestore", bestWorkout.toString());

                                    //Update the leaderboard with the best workout
                                    if (collection.getPath().equals(UserExercise.getWorkoutsDoc().getPath())) {
                                        String fieldpath = Objects.requireNonNull(bestWorkout.get("name")) + "." + User.getId();
                                        bestWorkout.put("id", User.getId());
                                        bestWorkout.put("username", User.getUsername());
                                        friendLeaderboard.update(fieldpath, bestWorkout);

                                    } else {
                                        String fieldpath = Objects.requireNonNull(bestWorkout.get("name")) + "." + acceptedId;
                                        bestWorkout.put("id", acceptedId);
                                        final String[] friendsusername = new String[1];

                                        newFriendDoc.get().addOnSuccessListener(documentSnapshot1 ->
                                                friendsusername[0] = (String) documentSnapshot1.get("username"));

                                        bestWorkout.put("username", friendsusername[0]);
                                        leaderboard.update(fieldpath, bestWorkout);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }

        executorService.shutdown();
    }

    public static String[] getExerciseArray() {
        return exerciseArray;
    }

}
