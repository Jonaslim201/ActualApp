package com.example.actualapp.userRelated;

import android.util.Log;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Leaderboard {

    private static CollectionReference leaderboardCollection;
    public static HashMap<WorkoutKey, ArrayList<FriendWorkout>> friendWorkoutMap;
    private static HashMap<String, DocumentSnapshot> leaderboardDocumentSnapshots;
    public static CollectionReference getLeaderboardCollection() {
        return leaderboardCollection;
    }

    public static void setLeaderboardCollection(CollectionReference leaderboardCollection) {
        Leaderboard.leaderboardCollection = leaderboardCollection;
    }


    public static ArrayList<FriendWorkout> getLeaderboard(WorkoutKey key){
        if (!friendWorkoutMap.containsKey(key)) {
            Log.d("Leaderboard", "null");
            return null;
        } else {
            ArrayList<FriendWorkout> friendWorkouts = (ArrayList<FriendWorkout>) friendWorkoutMap.get(key);
            Collections.sort(Objects.requireNonNull(friendWorkouts), new FriendWorkout.weightComparator());
            return friendWorkouts;
        }
    }

    public static void setFriendWorkoutMap(HashMap<WorkoutKey, ArrayList<FriendWorkout>> friendWorkoutMap) {
        Leaderboard.friendWorkoutMap = friendWorkoutMap;
    }

    public static void setLeaderboardDocumentSnapshots(HashMap<String, DocumentSnapshot> leaderboardDocumentSnapshots) {
        Leaderboard.leaderboardDocumentSnapshots = leaderboardDocumentSnapshots;
    }

    public static HashMap<String, DocumentSnapshot> getLeaderboardDocumentSnapshots() {
        return leaderboardDocumentSnapshots;
    }


    public static void updateLeaderboard(String category, String exerciseName, Map<String, Object> newRecord, FirestoreCallBack callBack) {
        WorkoutKey key = new WorkoutKey(category, exerciseName);
        friendWorkoutMap.put(key, new ArrayList<>());

        CountDownLatch internalLatch = new CountDownLatch(newRecord.size());

        for (Object value : newRecord.values()) {
            if (value instanceof Map) {
                FriendWorkout friendWorkout = new FriendWorkout((Map<String, Object>) value);
                Objects.requireNonNull(friendWorkoutMap.get(key)).add(friendWorkout);
            }

            internalLatch.countDown();
            if (internalLatch.getCount() == 0) {
                callBack.onFirestoreResult(true);
            }
        }
    }
}
