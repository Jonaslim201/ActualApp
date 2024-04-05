package com.example.actualapp.exerciseRelated;

public class FriendWorkout extends Workout{

    private String id;
    private String username;
    private int profilePic;

    public FriendWorkout(String name, float weightLifted, String dateOfWorkout, int numOfReps, String id, String username, int profilePic) {
        super(name, weightLifted, dateOfWorkout, numOfReps);
        this.username = username;
        this.id = id;
        this.profilePic = profilePic;
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
}
