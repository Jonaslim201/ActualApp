package com.example.actualapp;

import java.util.ArrayList;


//Callback to return the List of Exercise Objects
public interface ExerciseCallBack {
    void onSuccessResult(ArrayList<Exercise> exercise);
}
