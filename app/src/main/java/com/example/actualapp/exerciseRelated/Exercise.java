package com.example.actualapp.exerciseRelated;


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

    public String getPriMuscleGroups() {
        return priMuscleGroups;
    }

    public String getSecMuscleGroups() {
        return secMuscleGroups;
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
