package com.example.actualapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.exerciseRelated.WorkoutCallback;
import com.example.actualapp.userRelated.UserExercise;
import com.example.actualapp.workoutListAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


//Individual Exercise pages, temp for Mike
public class ExerciseActivity extends AppCompatActivity {

    GraphView graphView;
    String exerciseName;
    String category;
    private RecyclerView mRecyclerView;
    ArrayList<Workout> workoutRecords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle names = intent.getExtras();
        exerciseName = names.getString("exerciseName");
        category = names.getString("category");
        setContentView(R.layout.individual_exercise);

        Log.d("ExerciseActivity", "running getWorkouts");
        UserExercise.getWorkouts(category, exerciseName, new WorkoutCallback() {
            @Override
            public void onSuccessResult(List<? extends Workout> workouts, boolean isEmpty) {
                Log.d("ExerciseActivity", "running getWorkouts");
                Log.d("ExerciseActivity", String.valueOf(isEmpty));
                if (!isEmpty){
                    workoutRecords = (ArrayList<Workout>) workouts;
                    graphInit();
                    recordsListInit();
                }
            }
        });
        //TODO: add toggle to switch to change to friends leaderboards

        //TODO: add addExerciseRecord Button click listener and function
        ExtendedFloatingActionButton addWorkout = findViewById(R.id.addExerciseRecord);
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myActivity = new Intent(ExerciseActivity.this, TempAddWorkoutActivity.class);
                Bundle intent = new Bundle();
                intent.putString("exerciseName", exerciseName);
                intent.putString("category", category);

                startActivity(myActivity);
            }
        });
    }

    private void recordsListInit() {
        mRecyclerView = findViewById(R.id.recordsList);
        workoutListAdapter mAdapter = new workoutListAdapter(this, workoutRecords);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void graphInit(){
        graphView = findViewById(R.id.recordsGraph);

        if(!workoutRecords.isEmpty()){
            DataPoint[] dataPoints=new DataPoint[workoutRecords.size()];
            Integer maxWeight=0;
            //Loop to create Datapoint ArrayList for series
            for (int i = 0; i < workoutRecords.size(); i++) {
                if (workoutRecords.get(i).getWeightLifted()>maxWeight){
                    maxWeight = Integer.valueOf((int) workoutRecords.get(i).getWeightLifted());
                }
                dataPoints[i]=new DataPoint(workoutRecords.get(i).dateCal.getTime(),Integer.valueOf((int) workoutRecords.get(i).getWeightLifted()));
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            series.setDrawDataPoints(true);
            graphView.addSeries(series);

            //pass Date objects to DataPoint-Constructor
            //this will convert the Date to double via Date#getTime()
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5 because of the space

            //Get Date of date one month ago from most recent record
            Calendar cal= Calendar.getInstance();
            cal.setTime(workoutRecords.get(workoutRecords.size()-1).dateCal.getTime());
            cal.add(cal.DATE,-31);
            Date temp=cal.getTime();
            //Set X bounds to show one month from most recent
            graphView.getViewport().setMinX(temp.getTime());
            graphView.getViewport().setMaxX(dataPoints[dataPoints.length-1].getX());
            graphView.getViewport().setXAxisBoundsManual(true);
            //Set Y bounds to show 0 to max weight+50kg
            graphView.getViewport().setMinY(0);
            graphView.getViewport().setMaxY(maxWeight+50);
            graphView.getViewport().setYAxisBoundsManual(true);
            //Set Title
            graphView.setTitle(exerciseName);
            graphView.setTitleTextSize(64);
            //Set scrollable horizontal
            graphView.getViewport().setScrollable(true);
            // as we use dates as labels, the human rounding to nice readable numbers is not necessary
            graphView.getGridLabelRenderer().setHumanRounding(false);
        }
    }
}