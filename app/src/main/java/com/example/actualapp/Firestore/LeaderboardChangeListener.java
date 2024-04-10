package com.example.actualapp.Firestore;

import com.example.actualapp.exerciseRelated.WorkoutKey;

public interface LeaderboardChangeListener {
    void onLeaderboardChanged(WorkoutKey key);
}
