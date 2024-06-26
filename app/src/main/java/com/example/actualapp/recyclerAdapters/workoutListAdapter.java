package com.example.actualapp.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.Workout;

import java.util.ArrayList;
import java.util.Collections;


public class workoutListAdapter extends RecyclerView.Adapter<workoutListAdapter.recordViewHolder> {
    LayoutInflater mInflater;
    ArrayList<Workout> workoutRecords;

    public workoutListAdapter(Context context, ArrayList<Workout> records) {
        mInflater = LayoutInflater.from(context);
        this.workoutRecords = records;
        Collections.sort(workoutRecords, Collections.reverseOrder());
    }
    @NonNull
    @Override
    public recordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // Inflate an item view.
        View mItemView =
                mInflater.inflate(R.layout.records_list_layout,
                        parent, false);
        return new recordViewHolder(mItemView);
    }


    @Override
    public void onBindViewHolder(@NonNull recordViewHolder holder, int position) {
        // Retrieve the data for that position

        Workout mCurrent = workoutRecords.get(position);
        mCurrent.setDate();
        // Add the data to the view
        holder.exerciseDate.setText(mCurrent.DayMonthYear());
        holder.exerciseRep.setText(mCurrent.getNumOfReps()+" reps");
        holder.exerciseWeight.setText(mCurrent.getWeightLifted()+"kg");
    }
    @Override
    public int getItemCount() {
        return workoutRecords.size();
    }

    public static class recordViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseDate;
        TextView exerciseRep;
        TextView exerciseWeight;

        public recordViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseDate = itemView.findViewById(R.id.exerciseDate);
            exerciseRep = itemView.findViewById(R.id.exerciseRep);
            exerciseWeight = itemView.findViewById(R.id.exerciseWeight);
        }
    }

}