package com.example.actualapp.exerciseRelated;


//Subclass for Exercise, one Object is one Workout session the user did
public class Workout extends Exercise{
    private float weightLifted;
    private String dateOfWorkout;
    private int numOfReps;
    public Workout(String name, float weightLifted, String dateOfWorkout, int numOfReps){
        super(name);
        this.weightLifted = weightLifted;
        this.dateOfWorkout = dateOfWorkout;
        this.numOfReps = numOfReps;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public float getWeightLifted() {
        return weightLifted;
    }

    public void setWeightLifted(float weightLifted) {
        this.weightLifted = weightLifted;
    }

    public int getNumOfReps() {
        return numOfReps;
    }

    public void setNumOfReps(int numOfReps) {
        this.numOfReps = numOfReps;
    }

    public String getDateOfWorkout() {
        return dateOfWorkout;
    }

    public void setDateOfWorkout(String dateOfWorkout) {
        this.dateOfWorkout = dateOfWorkout;
    }

}
