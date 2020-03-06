package com.projektarbeit.objects;

public class User {

    private String userId;
    private boolean isAdult;

    public User(String userId, boolean isAdult) {
        this.userId = userId;
        this.isAdult = isAdult;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdult() {
        return isAdult;
    }
}
