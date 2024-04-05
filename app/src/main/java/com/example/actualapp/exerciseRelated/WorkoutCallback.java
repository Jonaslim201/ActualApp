package com.example.actualapp.exerciseRelated;

import java.util.List;

public interface WorkoutCallback {
    void onSuccessResult(List<FriendWorkout> workouts, boolean isEmpty);
}
