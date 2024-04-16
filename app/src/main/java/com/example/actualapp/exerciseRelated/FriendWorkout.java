package com.example.actualapp.exerciseRelated;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.actualapp.R;

import java.util.Comparator;
import java.util.Map;

public class FriendWorkout extends Workout implements Parcelable {

    private String id;
    private String username;
    private int profilePic;

    public FriendWorkout(String category, String name, float weightLifted, String dateOfWorkout, int numOfReps, String id, String username, int profilePic) {
        super(category, name, weightLifted, dateOfWorkout, numOfReps);
        this.username = username;
        this.id = id;
        this.profilePic = profilePic;
    }

    public FriendWorkout(Workout workout, String id, String username, int profilePic){
        super(workout.getCategory(),workout.getName(), workout.getWeightLifted(), workout.getDateOfWorkout(), workout.getNumOfReps());
        Log.d("FriendWorkout", workout.getCategory());
        this.username = username;
        this.id = id;
        this.profilePic = profilePic;
    }

    public FriendWorkout(Map<String, Object> map){
        super((String) map.get("category"), (String) map.get("name"), Float.parseFloat(map.get("weightLifted").toString()), (String) map.get("dateOfWorkout"), Integer.parseInt(map.get("numOfReps").toString()));
        this.username = (String) map.get("username");
        this.id = (String) map.get("id");
        this.profilePic = R.drawable.baseline_person_24;
    }

    public FriendWorkout(){
        super("","", 0, "", 0);
        this.username = "";
        this.id = "";
        this.profilePic = R.drawable.baseline_person_24;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getProfilePic() {
        return profilePic;
    }

    @Override
    public String toString() {
        return "FriendWorkout{" +
                "username='" + username + '\'' +
                ", profilePic=" + profilePic +
                ", weightLifted=" + getWeightLifted() +
                ", numOfReps=" + getNumOfReps() +
                ", dateOfWorkout='" + getNumOfReps() + '\'' +
                '}';
    }

    protected FriendWorkout(Parcel in) {
        super(in);
        id = in.readString();
        username = in.readString();
        profilePic = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(username);
        dest.writeInt(profilePic);
    }

    public static final Creator<FriendWorkout> CREATOR = new Creator<FriendWorkout>() {
        @Override
        public FriendWorkout createFromParcel(Parcel in) {
            return new FriendWorkout(in);
        }

        @Override
        public FriendWorkout[] newArray(int size) {
            return new FriendWorkout[size];
        }
    };

    public static class weightComparator implements Comparator<FriendWorkout>{
        @Override
        public int compare(FriendWorkout o1, FriendWorkout o2) {
            int weightComparison = Float.compare(o2.getWeightLifted(),o1.getWeightLifted());

            if (weightComparison != 0) {
                return weightComparison;
            }

            return Integer.compare(o2.getNumOfReps(), o1.getNumOfReps());
        }
    }

}
