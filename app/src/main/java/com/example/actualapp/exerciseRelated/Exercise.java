package com.example.actualapp.exerciseRelated;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

//Each exercise has its individual class
public class Exercise implements Parcelable {

    private String category;
    private String name;
    private String priMuscleGroups;
    private String secMuscleGroups;
    public Exercise(String name, String priMuscleGroups, String secMuscleGroups){
        this.name = name;
        this.priMuscleGroups = priMuscleGroups;
        this.secMuscleGroups = secMuscleGroups;
    }

    public Exercise(String category, String name){
        this.category = category;
        this.name = name;
    }

    protected Exercise(Parcel in) {
        name = in.readString();
        priMuscleGroups = in.readString();
        secMuscleGroups = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPriMuscleGroups() {
        return priMuscleGroups;
    }

    public String getSecMuscleGroups() {
        return secMuscleGroups;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(priMuscleGroups);
        dest.writeString(secMuscleGroups);
    }

    public static class ExerciseBuilder{
        private String name;
        private String priMuscleGroups;
        private String secMuscleGroups;

        public ExerciseBuilder(){
        }

        public ExerciseBuilder setPriMuscleGroups(String priMuscleGroups) {
            this.priMuscleGroups = priMuscleGroups;
            return this;
        }

        public ExerciseBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ExerciseBuilder setSecMuscleGroups(String secMuscleGroups) {
            this.secMuscleGroups = secMuscleGroups;
            return this;
        }

        public Exercise Build(){
            return new Exercise(name, priMuscleGroups, secMuscleGroups);
        }
    }


}
