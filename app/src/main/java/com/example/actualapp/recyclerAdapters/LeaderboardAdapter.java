package com.example.actualapp.recyclerAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;

import java.util.ArrayList;


public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    LayoutInflater mInflater;
    private ArrayList<FriendWorkout> leaderboard;

    public LeaderboardAdapter(Context context, ArrayList<FriendWorkout> leaderboard) {
        mInflater = LayoutInflater.from(context);
        this.leaderboard = leaderboard;
    }

    public void setLeaderboard(ArrayList<FriendWorkout> leaderboard) {
        this.leaderboard = leaderboard;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.leaderboard_individual_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("leaderboardAdapter", leaderboard.get(position).toString());
        FriendWorkout workout = leaderboard.get(position);

        holder.textRank.setText(String.valueOf(position + 1));
        holder.textUsername.setText(workout.getUsername());
        // set the profile photo for user
        holder.imageProfile.setImageResource(workout.getProfilePic());
        // adding weight lifted, no. of reps, and date as subtext
        String details = "Weight Lifted: " + workout.getWeightLifted() + "kg\n" +
                "No. of Reps: " + workout.getNumOfReps() + "\n" +
                "Date: " + workout.getDateOfWorkout()+"\n";
        Log.d("leaderboardAdapter", details);
        holder.textDetails.setText(details);

        holder.textRank.setTextColor(mInflater.getContext().getResources().getColor(R.color.black));
        holder.textUsername.setTextColor(mInflater.getContext().getResources().getColor(R.color.black));
        holder.textDetails.setTextColor(mInflater.getContext().getResources().getColor(R.color.black));

        if (position == 0) {
            holder.itemView.setBackgroundResource(R.color.firstPlace);
        } else if (position == 1) {
            holder.itemView.setBackgroundResource(R.color.secondPlace);
        } else if (position == 2) {
            holder.itemView.setBackgroundResource(R.color.thirdPlace);
        } else {
            holder.itemView.setBackgroundResource(R.color.matteWhite);
        }
    }

    @Override
    public int getItemCount() {
        return leaderboard.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textRank, textUsername, textDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.text_rank);
            textUsername = itemView.findViewById(R.id.text_username);
            imageProfile = itemView.findViewById(R.id.image_profile);
            textDetails = itemView.findViewById(R.id.text_details); // Add this line
        }
    }
}
