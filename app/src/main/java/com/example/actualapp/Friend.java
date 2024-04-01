package com.example.actualapp;

public class Friend {

    private String username;
    private String id;
    private String email;

    public Friend(String username, String id, String email){
        this.username = username;
        this.id  = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
