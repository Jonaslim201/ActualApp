package com.example.actualapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.Workout;
import com.example.actualapp.recyclerAdapters.workoutListAdapter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class workoutFragment extends Fragment {

    private View workoutView;
    private RecyclerView mRecyclerView;
    ArrayList<Workout> workoutRecords;
    GraphView graphView;

    String exerciseName;
    String category;

    public workoutFragment(String exerciseName, String category, ArrayList<Workout> workoutRecords){
        this.exerciseName = exerciseName;
        this.category = category;
        this.workoutRecords = workoutRecords;
    }

    public interface InitializationListener {
        void onInitializationComplete();
    }

    private InitializationListener initializationListener;

    public void setInitializationListener(InitializationListener listener) {
        this.initializationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        workoutView = inflater.inflate(R.layout.graphview_workouts_layout, container, false);

        if(workoutRecords != null && !workoutRecords.isEmpty()){
            recordsListInit();
            graphInit(new FirestoreCallBack() {
                @Override
                public void onFirestoreResult(boolean success) {
                    if (initializationListener != null && success) {
                        initializationListener.onInitializationComplete();
                    }
                }
            });
        }
        return workoutView;
    }

    private void recordsListInit() {

        mRecyclerView = workoutView.findViewById(R.id.recordsList);
        workoutListAdapter mAdapter = new workoutListAdapter(workoutView.getContext(), workoutRecords);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(workoutView.getContext()));

    }

    private void graphInit(FirestoreCallBack callBack){
        graphView = workoutView.findViewById(R.id.recordsGraph);
        Collections.sort(workoutRecords);

        DataPoint[] dataPoints=new DataPoint[workoutRecords.size()];
        float maxWeight=0;
        //Loop to create Datapoint ArrayList for series
        for (int i = 0; i < workoutRecords.size(); i++) {
            if (workoutRecords.get(i).getWeightLifted()>maxWeight){
                maxWeight = workoutRecords.get(i).getWeightLifted();
            }

            dataPoints[i]=new DataPoint(workoutRecords.get(i).dateCal.getTime(),workoutRecords.get(i).getWeightLifted());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        series.setDrawDataPoints(true);
        graphView.addSeries(series);

        //pass Date objects to DataPoint-Constructor
        //this will convert the Date to double via Date#getTime()
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(workoutView.getContext()));
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
        callBack.onFirestoreResult(true);

    }
}
