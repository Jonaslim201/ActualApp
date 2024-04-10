package com.example.actualapp.exerciseRelated;

import java.util.HashMap;
import java.util.List;

public interface ExerciseNamesCallback {
    void onSuccessResult(HashMap<String, List<String>> exerciseNames);
}
