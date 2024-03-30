//package com.example.actualapp;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//
//public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
//
//    private List<Workout> leaderboard;
//
//    public LeaderboardAdapter(List<Workout> leaderboard) {
//        this.leaderboard = leaderboard;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Workout workout = leaderboard.get(position);
//        User user = workout.getUser();
//
//        holder.textRank.setText(String.valueOf(position + 1));
//        holder.textUsername.setText(workout.getUser().getName());
//        // set the profile photo for user
//        holder.imageProfile.setImageResource(user.getProfilePhoto());
//        // adding weight lifted, no. of reps, and date as subtext
//        String details = "Weight Lifted: " + workout.getWeightLifted() + "kg\n" +
//                "No. of Reps: " + workout.getNumOfReps() + "\n" +
//                "Date: " + formatDate(workout.getDateOfWorkout())+"\n";
//        holder.textDetails.setText(details);
//
//        if (position == 0) {
//            holder.itemView.setBackgroundResource(R.drawable.rounded_rectangle_first_place);
//        } else if (position == 1) {
//            holder.itemView.setBackgroundResource(R.drawable.rounded_rectangle_second_place);
//        } else if (position == 2) {
//            holder.itemView.setBackgroundResource(R.drawable.rounded_rectangle_third_place);
//        } else {
//            // background color for other ranking
//            holder.itemView.setBackgroundResource(R.drawable.rounded_rectangle);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return leaderboard.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView imageProfile;
//        TextView textRank, textUsername, textDetails;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textRank = itemView.findViewById(R.id.text_rank);
//            textUsername = itemView.findViewById(R.id.text_username);
//            imageProfile = itemView.findViewById(R.id.image_profile);
//            textDetails = itemView.findViewById(R.id.text_details); // Add this line
//        }
//    }
//
//    private String formatDate(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
//        return sdf.format(date);
//    }
//}
