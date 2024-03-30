package com.example.actualapp;


//Each exercise has its individual class
public class Exercise {

    private String name;
    private String priMuscleGroups;
    private String secMuscleGroups;

    public Exercise(String name, String priMuscleGroups, String secMuscleGroups){
        this.name = name;
        this.priMuscleGroups = priMuscleGroups;
        this.secMuscleGroups = secMuscleGroups;
    }

    public Exercise(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriMuscleGroups(String priMuscleGroups) {
        this.priMuscleGroups = priMuscleGroups;
    }

    public String getPriMuscleGroups() {
        return priMuscleGroups;
    }

    public String getSecMuscleGroups() {
        return secMuscleGroups;
    }


}
