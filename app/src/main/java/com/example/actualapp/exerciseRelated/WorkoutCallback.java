package com.example.actualapp.exerciseRelated;

import java.util.List;

public interface WorkoutCallback {
    void onSuccessResult(List<? extends Workout> workouts, boolean isEmpty);
}
