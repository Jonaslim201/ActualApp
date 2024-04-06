package com.example.actualapp.exerciseRelated;


import java.util.Calendar;

//Subclass for Exercise, one Object is one Workout session the user did
public class Workout extends Exercise implements Comparable<Workout>{
    private float weightLifted;
    private String dateOfWorkout;
    private int numOfReps;
    private String dayMonthYear;
    Calendar calendar;
    int day;
    int month;
    int year;
    int hours;
    int minutes;
    int seconds;
    public Calendar dateCal;

    public Workout(String name, float weightLifted, String dateOfWorkout, int numOfReps){
        super(name);
        this.weightLifted = weightLifted;
        this.dateOfWorkout = dateOfWorkout;
        this.numOfReps = numOfReps;
        this.dateCal = Calendar.getInstance();
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

    public String getDayMonthYear() {
        return dayMonthYear;
    }

    public void setDate(){
        dayMonthYear = dateOfWorkout.substring(0,11);
        this.day = Integer.valueOf(dateOfWorkout.substring(0,2));
        getMonth(dateOfWorkout);
        this.year=Integer.valueOf(dateOfWorkout.substring(7,11));
        this.hours=Integer.valueOf(dateOfWorkout.substring(12,14));
        this.minutes=Integer.valueOf(dateOfWorkout.substring(15,17));
        this.seconds=Integer.valueOf(dateOfWorkout.substring(18,20));
        this.dateCal.set(year,month,day,hours,minutes,seconds);
    }

    public void getMonth(String date) {
        switch (date.substring(3,6)){
            case "Jan":
                this.month = 1;
                break;
            case "Feb":
                this.month = 2;
                break;
            case "Mar":
                this.month = 3;
                break;
            case "Apr":
                this.month = 4;
                break;
            case "May":
                this.month = 5;
                break;
            case "Jun":
                this.month = 6;
                break;
            case "Jul":
                this.month = 7;
                break;
            case "Aug":
                this.month = 8;
                break;
            case "Sep":
                this.month = 9;
                break;
            case "Oct":
                this.month = 10;
                break;
            case "Nov":
                this.month = 11;
                break;
            case "Dec":
                this.month = 12;
                break;
        }
    }

    @Override
    public int compareTo(Workout o) {
        return this.dateCal.compareTo(o.dateCal);
    }
}
