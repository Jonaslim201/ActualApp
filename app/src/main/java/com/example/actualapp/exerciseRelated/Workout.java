package com.example.actualapp.exerciseRelated;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Map;

//Subclass for Exercise, one Object is one Workout session the user did
public class Workout extends Exercise implements Comparable<Workout>, Parcelable {
    private float weightLifted;
    private String dateOfWorkout;
    private int numOfReps;
    private String dayMonthYear;
    int day;
    int month;
    int year;
    int hours;
    int minutes;
    int seconds;
    Calendar dateCal;

    public Workout(String category, String name, float weightLifted, String dateOfWorkout, int numOfReps){
        super(category, name);
        this.weightLifted = weightLifted;
        this.dateOfWorkout = dateOfWorkout;
        this.numOfReps = numOfReps;
        setDate();
    }

    public Workout(Map<String, Object> map){
        super((String) map.get("category"),(String) map.get("name"));
        this.weightLifted = Float.parseFloat(map.get("weightLifted").toString());
        this.dateOfWorkout = (String) map.get("dateOfWorkout");
        this.numOfReps = Integer.parseInt(map.get("numOfReps").toString());
        setDate();
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

    public String DayMonthYear() {
        return dayMonthYear;
    }

    public void setDate(){
        dayMonthYear = dateOfWorkout.substring(0,11);
        this.day = Integer.valueOf(dateOfWorkout.substring(0,2));
        Month(dateOfWorkout);
        this.year=Integer.valueOf(dateOfWorkout.substring(7,11));
        this.hours=Integer.valueOf(dateOfWorkout.substring(12,14));
        this.minutes=Integer.valueOf(dateOfWorkout.substring(15,17));
        this.seconds=Integer.valueOf(dateOfWorkout.substring(18,20));
        dateCal = Calendar.getInstance();
        dateCal.set(year,month,day,hours,minutes,seconds);
    }

    public Calendar dateCal(){
        return dateCal;
    }

    public void Month(String date) {
        switch (date.substring(3,6)){
            case "Jan":
                this.month = 0;
                break;
            case "Feb":
                this.month = 1;
                break;
            case "Mar":
                this.month = 2;
                break;
            case "Apr":
                this.month = 3;
                break;
            case "May":
                this.month = 4;
                break;
            case "Jun":
                this.month = 5;
                break;
            case "Jul":
                this.month = 6;
                break;
            case "Aug":
                this.month = 7;
                break;
            case "Sep":
                this.month = 8;
                break;
            case "Oct":
                this.month = 9;
                break;
            case "Nov":
                this.month = 10;
                break;
            case "Dec":
                this.month = 11;
                break;
        }
    }

    @Override
    public int compareTo(Workout o) {
        return this.dateCal().compareTo(o.dateCal());
    }

    protected Calendar getDateCal() {
        return dateCal;
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public Workout(Parcel in) {
        super(in);
        weightLifted = in.readFloat();
        dateOfWorkout = in.readString();
        numOfReps = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeFloat(weightLifted);
        dest.writeString(dateOfWorkout);
        dest.writeInt(numOfReps);
    }
}
