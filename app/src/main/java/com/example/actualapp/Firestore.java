package com.example.actualapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.lang.Object;
import java.util.Random;



public class Firestore implements LoginAndRegisterCallBack {
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db;
    private static holdDocument userDocument;
    public static User currentUser;

    Firestore(){

    }

    //Initializes Firestore
    private static void initializeDatabase(Context activity){
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
    }

    //Start of register user function
    public static void registerUser(Map user, Context activity, LoginAndRegisterCallBack callback, boolean Registering){
        //Gets inputted username
        String username = (String) user.get("username");
        Log.d("Debug", username);

        //Check if the username exists
        Log.d("Debug", "Checking user");
        checkUser(username, activity, user, callback, Registering);
    }

    //Filters if username exists or no
    private static void handleRegistration(Context activity, Map user, String username, LoginAndRegisterCallBack callback) {
        Log.d("register", "entered handle register");

        //If username exists, return False on callback
        if (userDocument.isDocumentFound()){
            Log.d("register", "Username exists");
            callback.onFirestoreResult(false);
            Toast.makeText(activity, "Username already exists.", Toast.LENGTH_SHORT).show();
        } else { //If username does noe exist, get unique ID for user
            checkIDRecursively(activity, user, username, callback);
        }
    }

    //Finding unique ID
    private static void checkIDRecursively(Context activity, Map user, String username, LoginAndRegisterCallBack callback) {
        final String id = generateId();
        getID(user, id, new LoginAndRegisterCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success) {
                    Log.d("hi", "FUCK id exists L");
                    // Call the method recursively if randomly generated ID exists
                    checkIDRecursively(activity, user, username, callback);
                } else {
                    Log.d("hi", "id DONT EXIST BABY WOOOOOOOOOO");

                    //Puts id in the HashMap with the other information and completes Registration
                    user.put("id", id);
                    completeRegistration(activity, user, username, callback);
                }
            }
        });
    }

    //Checks if generated ID already exists in the Firestore
    private static void getID(Map user, String id, LoginAndRegisterCallBack callBack){
        Query query = db.collection("appUsers").whereEqualTo("id", id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            private boolean uniqueIdFound = true;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("Debug", "Entered onComplete");
                if (task.isSuccessful()) {
                    Log.d("Debug", "Task was successful");
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d("Debug", "ID already exists");
                            //If ID exists, returns true on callback and continue recursion
                            callBack.onFirestoreResult(true);
                            break;
                        }
                    }
                } else {
                    Log.d("hi", "id dont exists SHEESH");
                    // Moves on with registration if ID does not exist
                    callBack.onFirestoreResult(false);
                }
            }
        });
    }

    //Adds all the User information to the Firestore and creates the Static User object
    private static void completeRegistration(Context activity, Map user, String username, LoginAndRegisterCallBack callback){
        //JUST ADDING A SAMPLE FOR WORKOUT OBJECT
        String dateInString = "31-Dec-1998 23:37:50";
        ArrayList<Workout> workouts = new ArrayList<Workout>();
        workouts.add(new Workout("testworkout", 30, dateInString, 3));
        user.put("workouts", workouts);
        //JUST ADDING A SAMPLE FOR WORKOUT OBJECT

        //Adds the Map into Firestore
        db.collection("appUsers").document(username).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

            //Adding into Firestore was successful
            @Override
            public void onSuccess(Void v) {
                Log.d("register", "Added");
                Toast.makeText(activity, "You have registered successfully!", Toast.LENGTH_SHORT).show();

                //Creates a Static User object to be used throughout the session of the app
                currentUser = new User(user.get("username").toString(),
                        user.get("password").toString(),
                        user.get("number").toString(),
                        user.get("email").toString());

                //Getting the reference to the newly created User document in Firestore
                db.collection("appUsers").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //Setting the reference to the User's Firestore document in the User oject
                            currentUser.setUserDoc(task.getResult());
                            callback.onFirestoreResult(true);
                        } else {
                            Log.d("Firestore", "Failed to retrieve user document");
                            callback.onFirestoreResult(false);
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("register", "Failed to add");
                Toast.makeText(activity, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                callback.onFirestoreResult(false);
            }
        });
    }

    //Randomly generates 6-character AlphaNum ID
    private static String generateId(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    //Start of Login function
    public static void loginUser(Map user, Context activity, LoginAndRegisterCallBack callback, boolean Registering) {
        String username = (String) user.get("username");
        Log.d("Debug", username);
        Log.d("Debug", "Checking user");

        //Checks if username exists in the Firestore
        checkUser(username, activity, user, callback, Registering);
    }


    //Checks password and makes Toast if failed to login. Send back false on callback
    private static void checkPassword(Context activity, Map user, LoginAndRegisterCallBack callback) {
        if (userDocument.isDocumentFound()) {
            try {
                if (userDocument.getFoundDocument().get("password").equals(user.get("password"))) {
                    initializeUserObject(activity, user, callback);

            } else {
                Toast.makeText(activity, "Wrong password.", Toast.LENGTH_SHORT).show();
                callback.onFirestoreResult(false);
            }

        } catch (NullPointerException e) {
            Toast.makeText(activity, "Please enter a value.", Toast.LENGTH_SHORT).show();
            callback.onFirestoreResult(false);
        }

    } else {
        Toast.makeText(activity, "Username not found.", Toast.LENGTH_SHORT).show();
        callback.onFirestoreResult(false);
        }
    }

    //Initializes the Static User object with all the user information
    private static void initializeUserObject(Context activity, Map user, LoginAndRegisterCallBack callback){
        //Getting reference to the user document
        DocumentSnapshot userDoc = userDocument.getFoundDocument();

        //Initializing the User object
        currentUser = new User(userDoc.get("username").toString(),
                userDoc.get("password").toString(),
                userDoc.get("email").toString(),
                userDoc.get("id").toString());

        //Setting the document reference in the User Object
        currentUser.setUserDoc(userDoc);


        //Gets the array of Workout objects from Firestore and adds it to the User object
        ArrayList<Map<String, Object>> workoutsData = (ArrayList<Map<String, Object>>) userDoc.get("workouts");
        ArrayList<Workout> workoutsObjects = new ArrayList<>();


        //Loops through the array and appends them to the User Object
        if (workoutsData != null) {
            for (Map<String, Object> objData : workoutsData) {
                String name = (String) objData.get("name");
                int numOfReps = ((Long) objData.get("numOfReps")).intValue();
                float weightLifted = ((Long) objData.get("weightLifted")).floatValue();
                String dateOfWorkout = (String) objData.get("dateOfWorkout");
                Workout workout = new Workout(name, weightLifted, dateOfWorkout, numOfReps);
                workoutsObjects.add(workout);
            }
            currentUser.setWorkouts(workoutsObjects);
        }

        callback.onFirestoreResult(true);
        Toast.makeText(activity, "Login successful.", Toast.LENGTH_SHORT).show();
    }


    //Checks if the username exists in the Firestore
    private static void checkUser(String username, Context activity, Map user, LoginAndRegisterCallBack callback, boolean Registering) {
        Firestore.userDocument = new holdDocument();
        initializeDatabase(activity);
        Query check = db.collection("appUsers").whereEqualTo("username", username);
        Log.d("Debug", "Query gotten");
        check.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("Debug", "Entered onComplete");
                    if (task.isSuccessful()) {
                        Log.d("Debug", "Task was successful");
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Log.d("Debug", "name already exists");
                                Firestore.userDocument.setFoundDocument(document);
                                break;
                            }
                        }
                    }
                    if(Registering) {
                        Log.d("register", "pushed to handleRegistration");
                        handleRegistration(activity, user, username, callback);
                    } else {
                        Log.d("register", "pushed to login");
                        checkPassword(activity, user, callback);
                    }

                }
            });

    }


    @Override
    public void onFirestoreResult(boolean success) {

    }

    //Getting the list of exercises for a category for the RecycleView
    public static void getExercises(Context activity, String category, ExerciseCallBack exerciseCallBack){
        initializeDatabase(activity);
        ArrayList<Exercise> exercises = new ArrayList<>();
        db.collection("ExerciseCategories").document(category).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                for(Map.Entry<String, Object> entry: map.entrySet()){
                    ArrayList<String> exercise = (ArrayList<String>) entry.getValue();
                    exercises.add(new Exercise(exercise.get(0), exercise.get(1), exercise.get(2)));
                    exerciseCallBack.onSuccessResult(exercises);
                }
            }
        });
    }
}

