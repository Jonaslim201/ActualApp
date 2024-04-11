package com.example.actualapp.Firestore;

import android.util.Log;

import com.example.actualapp.activities.ExerciseActivity;
import com.example.actualapp.exerciseRelated.ExerciseNamesCallback;
import com.example.actualapp.exerciseRelated.WorkoutKey;
import com.example.actualapp.userRelated.Leaderboard;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//ADD THESE METHODS TO THE HOMEPAGE
public class FirestoreListener extends Firestore{

    private static FirestoreListener instanceListener;
    private final List<ListenerRegistration> listeners = new ArrayList<>();
    private static final Set<WeakReference<LeaderboardChangeListener>> leaderboardListeners = new HashSet<>();

    private static FriendRequestChangeListener friendRequestListener;
    private static FriendListChangeListener friendListListener;

    private FirestoreListener(){
    }

    //Get the instance of the listener
    public static FirestoreListener getInstanceListener() {
        if (instanceListener == null){
            instanceListener = new FirestoreListener();
        }
        return instanceListener;
    }

    //Add a listener
    public void addListener(ListenerRegistration listener) {
        listeners.add(listener);
    }

    //Stop listeners
    public void stopListener(){
        if (!listeners.isEmpty()){
            listeners.clear();
        }
    }

    public static void setFriendRequestListener(FriendRequestChangeListener friendRequestListener) {
        FirestoreListener.friendRequestListener = friendRequestListener;
    }

    public static void unregisterFriendRequestListener(FriendRequestChangeListener friendRequestListener){
        FirestoreListener.friendRequestListener = null;
    }

    public static void setFriendListListener(FriendListChangeListener friendListListener) {
        FirestoreListener.friendListListener = friendListListener;
    }

    public static void unregisterFriendListListener(FriendListChangeListener friendListListener){
        FirestoreListener.friendListListener = null;
    }

    //Register the listener
    public static void registerLeaderboardListener(LeaderboardChangeListener listener){
        leaderboardListeners.add(new WeakReference<>(listener));
    }

    //Unregister the listener
    public static void unregisterLeaderboardListener(LeaderboardChangeListener listener){
        leaderboardListeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
    }

    //Notify the listeners of the leaderboard change
    public static void notifyLeaderboardListeners(WorkoutKey workoutKey){
        for (WeakReference<LeaderboardChangeListener> listener : leaderboardListeners){
            LeaderboardChangeListener leaderboardListener = listener.get();
            if (leaderboardListener != null){
                leaderboardListener.onLeaderboardChanged(workoutKey);
            }
        }
        leaderboardListeners.removeIf(ref -> ref.get() == null);
    }

    //Firestore Listener for Friend Requests
    public void friendReqListener(){

        //Add a listener for the friend requests of the user
        addListener(db.collection("appUsers").document(User.getUsername()).addSnapshotListener((amendedValue, error) -> {
            if (error != null){
                Log.d("FriendFirestore", "Failed to listen.");
            } else {
                Log.d("FriendFirestore", "start listen succeeded");
                //If the change is not null and exists, update friend requests
                if(amendedValue != null && amendedValue.exists() && amendedValue.contains("friendRequests")){

                    //Get the friend requests of the user
                    Map<String, Object> friendRequests = (Map<String, Object>) amendedValue.get("friendRequests");

                    if (friendRequests != null){
                        UserFriends.setReceivedFriendRequests((ArrayList<DocumentReference>) friendRequests.get("received"), new FirestoreCallBack() {
                            @Override
                            public void onFirestoreResult(boolean success) {
                                if (success && friendRequestListener != null){
                                    friendRequestListener.onFriendRequestChanged();
                                }
                            }
                        });
                        UserFriends.setSentFriendRequests((ArrayList<DocumentReference>) friendRequests.get("sent"));
                    }
                    Log.d("FriendFirestore", "listen succeeded");
                }

            }
        }));

    }

    //Firestore Listener for Friends
    public void friendsListener(){

        //Add a listener for the friends of the user
        addListener(db.collection("appUsers").document(User.getUsername()).addSnapshotListener((amendedValue, error) -> {
            if (error != null){
                Log.d("FriendFirestore", "Failed to listen.");
            } else {

                //If the change is not null and exists, update friends
                if(amendedValue != null && amendedValue.exists() && amendedValue.contains("friends")){

                    //Get the friends array of the user
                    List<DocumentReference> friends = (List<DocumentReference>) amendedValue.get("friends");
                    Log.d("FriendFirestore", "friends: " + friends.size());
                    if (friends != null && !friends.isEmpty()){
                        for (DocumentReference friendDoc : friends) {
                            if (friendDoc != null){
                                UserFriends.setFriendDocuments((ArrayList<DocumentReference>) friends);
                            }
                        }

                    }
                    Log.d("FriendFirestore", "listen succeeded");

                }
            }
        }));
    }

    //Firestore Listener for Leaderboard
    public void leaderboardListener(){

        //Get all exercise names
        ExerciseFirestore.getAllExerciseName(new ExerciseNamesCallback() {
            @Override
            public void onSuccessResult(HashMap<String, List<String>> allExerciseNames) {

                //For each category, add a listener
                for (String category:ExerciseFirestore.getExerciseArray()){
                    addListener(Leaderboard.getLeaderboardCollection().document(category)
                            .addSnapshotListener((snapshot, e) -> {
                                if (e != null) {
                                    Log.w("FirestoreError", "Listen failed.", e);
                                    return;
                                }

                                //If the change is not null and exists, update the leaderboard
                                if (snapshot != null && snapshot.exists()) {
                                    Log.d("FirestoreUpdate", "Current data: " + snapshot.getData());
                                    DocumentSnapshot prevValue;

                                    //Get the previous value
                                    if (Leaderboard.getLeaderboardDocumentSnapshots() != null){
                                        prevValue = Leaderboard.getLeaderboardDocumentSnapshots().get(category);
                                    } else {
                                        prevValue = null;
                                    }

                                    //Get the exercise names for the category
                                    List<String> exerciseNames = allExerciseNames.get(category);
                                    if (exerciseNames != null){ //ONLY HAVE NULLS FOR NOW CUZ OF UNFINISHED FIRESTORE

                                        //For each exercise name, check for updates
                                        for(String exerciseName : exerciseNames){
                                            Object value = snapshot.get(exerciseName);
                                            Object prevValueObj = (prevValue != null) ? prevValue.get(exerciseName) : null;

                                            //If the value is not null and is different from the previous value, update the leaderboard
                                            if (value != null && !value.equals(prevValueObj)) {
                                                Map<String, Object> newRecord = (Map<String, Object>) value;
                                                Leaderboard.updateLeaderboard(category, exerciseName, newRecord);

                                                if (ExerciseActivity.instance != null && ExerciseActivity.instance.isInitialized()) {
                                                    FirestoreListener.notifyLeaderboardListeners(new WorkoutKey(category, exerciseName));
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    Log.d("FirestoreUpdate", "Current data: null");
                                }
                            }));
                }
            }
        });
    }
}
