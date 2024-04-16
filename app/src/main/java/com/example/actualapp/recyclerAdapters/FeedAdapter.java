package com.example.actualapp.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.exerciseRelated.FriendWorkout;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{

    private static ArrayList<FriendWorkout> feedWorkouts;
    private Context activity;
    private Animation scaleAnimation;

    public FeedAdapter(ArrayList<FriendWorkout> feedWorkouts, Context activity){
        FeedAdapter.feedWorkouts = feedWorkouts;
        this.activity = activity;

        scaleAnimation = new ScaleAnimation(
                0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new BounceInterpolator());
    }

    public static void setFeedWorkouts(ArrayList<FriendWorkout> feedWorkouts) {
        FeedAdapter.feedWorkouts = feedWorkouts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_feed_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position) {

        FriendWorkout currWorkout = feedWorkouts.get(position);

        String exerciseName = currWorkout.getCategory() + ", " + currWorkout.getName();
        String weightLifted = "Weight Lifted: " + currWorkout.getWeightLifted() + "kg";
        String numOfReps = "Number of Reps: " + currWorkout.getNumOfReps();

        holder.username.setText(currWorkout.getUsername());
        holder.exerciseName.setText(exerciseName);
        holder.weightLifted.setText(weightLifted);
        holder.reps.setText(numOfReps);
        holder.date.setText(currWorkout.getDateOfWorkout());

        ImageView likeButton = holder.likeButton;

        likeButton.setTag(R.drawable.baseline_favorite_border_24);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the animation
                likeButton.startAnimation(scaleAnimation);

                // Change the image source based on the current state
                if ((Integer)likeButton.getTag() == R.drawable.baseline_favorite_border_24) {
                    likeButton.setImageResource(R.drawable.baseline_favorite_24); // This is your filled heart drawable
                    likeButton.setTag(R.drawable.baseline_favorite_24);
                } else {
                    likeButton.setImageResource(R.drawable.baseline_favorite_border_24); // This is your outlined heart drawable
                    likeButton.setTag(R.drawable.baseline_favorite_border_24);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedWorkouts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView feedCardView;
        TextView username;
        TextView exerciseName;
        TextView weightLifted;
        TextView reps;
        TextView date;
        ImageView likeButton;
        ImageView shareButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feedCardView = itemView.findViewById(R.id.feedCardView);
            username = itemView.findViewById(R.id.feedUsername);
            exerciseName = itemView.findViewById(R.id.feedExercisename);
            weightLifted = itemView.findViewById(R.id.feedWeightLifted);
            reps = itemView.findViewById(R.id.feedNumOfReps);
            date = itemView.findViewById(R.id.feedDateOfWorkout);
            likeButton = itemView.findViewById(R.id.feedLikeButton);
            shareButton = itemView.findViewById(R.id.feedShareButton);
        }
    }
}
