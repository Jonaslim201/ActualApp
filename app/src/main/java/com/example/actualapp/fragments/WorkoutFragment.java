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
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.example.actualapp.recyclerAdapters.workoutListAdapter;
import com.example.actualapp.userRelated.UserExercise;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class WorkoutFragment extends Fragment {

    private View workoutView;
    private workoutListAdapter mAdapter;
    ArrayList<Workout> workoutRecords;
    GraphView graphView;

    String exerciseName;
    String category;

    public static WorkoutFragment newInstance(String exerciseName, String category, ArrayList<Workout> workoutRecords){
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString("exerciseName", exerciseName);
        args.putString("category", category);
        args.putParcelableArrayList("workoutRecords", workoutRecords);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkoutFragment(){
    }

    public interface InitializationListener {
        void onInitializationComplete();
    }

    private InitializationListener initializationListener;

    public void setInitializationListener(InitializationListener listener) {
        this.initializationListener = listener;
    }

    public void removeInitializationListener() {
        this.initializationListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        workoutView = inflater.inflate(R.layout.graphview_workouts_layout, container, false);

        Bundle args = getArguments();
        if (args != null) {
            this.exerciseName = args.getString("exerciseName");
            this.category = args.getString("category");
            this.workoutRecords = args.getParcelableArrayList("workoutRecords");
        }

        if(workoutRecords != null && !workoutRecords.isEmpty()){
            workoutView.findViewById(R.id.noRecords).setVisibility(View.GONE);
            recordsListInit();
            graphInit(success -> {
                if (initializationListener != null && success) {
                    initializationListener.onInitializationComplete();
                }
            });
        } else {
            workoutView.findViewById(R.id.noRecords).setVisibility(View.VISIBLE);
        }

        return workoutView;
    }

    private void recordsListInit() {
        RecyclerView mRecyclerView = workoutView.findViewById(R.id.recordsList);
        mAdapter = new workoutListAdapter(workoutView.getContext(), workoutRecords);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(workoutView.getContext()));
    }

    private void graphInit(FirestoreCallBack callBack){
        graphView = workoutView.findViewById(R.id.recordsGraph);
        Collections.sort(workoutRecords);

        graphView.removeAllSeries();
        DataPoint[] dataPoints=new DataPoint[workoutRecords.size()];
        float maxWeight = 0;
        //Loop to create Datapoint ArrayList for series
        for (int i = 0; i < workoutRecords.size(); i++) {
            if (workoutRecords.get(i).getWeightLifted()>maxWeight){
                maxWeight = workoutRecords.get(i).getWeightLifted();
            }
            dataPoints[i]=new DataPoint(workoutRecords.get(i).dateCal().getTime(),workoutRecords.get(i).getWeightLifted());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        series.setDrawDataPoints(true);
        graphView.addSeries(series);

        //pass Date objects to DataPoint-Constructor
        //this will convert the Date to double via Date#getTime()
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(workoutView.getContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5 because of the space

        //Get Date of date one month ago from most recent record
        Calendar cal = Calendar.getInstance();
        cal.setTime(workoutRecords.get(workoutRecords.size()-1).dateCal().getTime());
        cal.add(cal.DATE,-31);
        Date temp = cal.getTime();
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

    private void updateGraphView(Workout workout) {
        // Create a new DataPoint for the new workout
        DataPoint newDataPoint = new DataPoint(workout.dateCal().getTime(), workout.getWeightLifted());

        // Add the new DataPoint to the existing series
        LineGraphSeries<DataPoint> series = (LineGraphSeries<DataPoint>) graphView.getSeries().get(0);
        series.appendData(newDataPoint, true, workoutRecords.size());

        // Update the Y-axis bounds if necessary
        double maxY = graphView.getViewport().getMaxY(false);
        if (workout.getWeightLifted() > maxY) {
            graphView.getViewport().setMaxY(workout.getWeightLifted() + 50);
        }

        // Update the X-axis bounds
        graphView.getViewport().setMaxX(newDataPoint.getX());
    }

    public void addWorkout(Workout workout, WorkoutKey key) {

        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (workoutRecords == null) {
                        workoutRecords = new ArrayList<>();
                    }

                    workoutRecords = UserExercise.getWorkouts(key);

                    if (mAdapter == null) {
                        recordsListInit();
                    } else {
                        mAdapter.notifyItemInserted(workoutRecords.size());
                    }

                    if (graphView == null){
                        graphInit(success -> {
                            if (initializationListener != null && success) {
                                initializationListener.onInitializationComplete();
                            }
                        });
                    } else {
                        updateGraphView(workout);
                    }
                }
            });

        }

    }
}
