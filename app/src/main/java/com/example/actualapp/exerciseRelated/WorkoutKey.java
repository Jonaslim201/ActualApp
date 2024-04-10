package com.example.actualapp.exerciseRelated;

import androidx.annotation.NonNull;

import java.util.Objects;

public class WorkoutKey {
    private final String category;
    private final String exerciseName;

    public WorkoutKey(String workoutType, String exerciseName) {
        this.category = workoutType;
        this.exerciseName = exerciseName;
    }

    public String getCategory() {
        return category;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkoutKey that = (WorkoutKey) o;
        return category.equals(that.category) &&
                exerciseName.equals(that.exerciseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, exerciseName);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkoutKey{" +
                "workoutType='" + category + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                '}';
    }

    public String getFieldPath(){
        return category + "." + exerciseName;
    }
}
