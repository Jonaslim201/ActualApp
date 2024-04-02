package com.example.actualapp.userRelated;

import com.google.firebase.firestore.DocumentSnapshot;


//User class with static fields to allow the information to be accessed throughout the app session
public class User {
    private static String username;
    private static String password;
    private static String email;

    //Unique ID, will be used as a add friend code
    private static String id;

    //Reference to the user document in Firestore
    private static DocumentSnapshot userDoc;

    public User(UserBuilder userBuilder){
        User.username = userBuilder.username;
        User.password = userBuilder.password;
        User.email = userBuilder.email;
        User.id = userBuilder.id;
        User.userDoc = userBuilder.userDoc;
    }

    public User(){
    }

    public static DocumentSnapshot getUserDoc() {
        return userDoc;
    }

    public static String getPassword(){
        return password;
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getId() {
        return id;
    }

    public static class UserBuilder {
        private String username;
        private String password;
        private String email;
        private String id;
        private DocumentSnapshot userDoc;

        public UserBuilder() {
        }

        public UserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public UserBuilder setUserDoc(DocumentSnapshot userDoc) {
            this.userDoc = userDoc;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }

}
