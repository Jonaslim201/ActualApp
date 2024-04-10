package com.example.actualapp.userRelated;

import android.util.Log;

import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leaderboard {

    private static CollectionReference leaderboardCollection;
    private static HashMap<WorkoutKey, List<FriendWorkout>> friendWorkoutMap;
    private static HashMap<String, DocumentSnapshot> leaderboardDocumentSnapshots;
    public static CollectionReference getLeaderboardCollection() {
        return leaderboardCollection;
    }

    public static void setLeaderboardCollection(CollectionReference leaderboardCollection) {
        Leaderboard.leaderboardCollection = leaderboardCollection;
    }

    public static ArrayList<FriendWorkout> getLeaderboard(WorkoutKey key){
        Log.d("Leaderboard", "getting leaderboard");
        Log.d("Leaderboard", key.toString());

        if (friendWorkoutMap.get(key) == null) {
            Log.d("Leaderboard", "null");
            return null;
        } else {
            Log.d("Leaderboard", friendWorkoutMap.get(key).toString());
            ArrayList<FriendWorkout> friendWorkouts = (ArrayList<FriendWorkout>) friendWorkoutMap.get(key);
            Collections.sort(friendWorkouts, new FriendWorkout.weightComparator());
            Log.d("Leaderboard", friendWorkouts.toString());
            return friendWorkouts;
        }
    }

    public static void setFriendWorkoutMap(HashMap<WorkoutKey, List<FriendWorkout>> friendWorkoutMap) {
        Leaderboard.friendWorkoutMap = friendWorkoutMap;
    }

    public static void setLeaderboardDocumentSnapshots(HashMap<String, DocumentSnapshot> leaderboardDocumentSnapshots) {
        Leaderboard.leaderboardDocumentSnapshots = leaderboardDocumentSnapshots;
    }

    public static HashMap<String, DocumentSnapshot> getLeaderboardDocumentSnapshots() {
        return leaderboardDocumentSnapshots;
    }

    public static HashMap<WorkoutKey, List<FriendWorkout>> getFriendWorkoutMap() {
        return friendWorkoutMap;
    }

    public static void updateLeaderboard(String category, String exerciseName, Map<String, Object> newRecord) {
        WorkoutKey key = new WorkoutKey(category, exerciseName);
        friendWorkoutMap.put(key, new ArrayList<FriendWorkout>());

        for (Object value : newRecord.values()) {
            Log.d("Leaderboard", value.toString());
            if (value instanceof Map) {
                FriendWorkout friendWorkout = new FriendWorkout((Map<String, Object>) value);
                friendWorkoutMap.get(key).add(friendWorkout);
            }
        }
    }
}
