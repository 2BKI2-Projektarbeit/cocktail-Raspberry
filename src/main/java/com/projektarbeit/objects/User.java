package com.projektarbeit.objects;

public class User {

    private String userId;
    private boolean isAdult;
    private boolean isAdmin;

    public User(String userId, boolean isAdult, boolean isAdmin) {
        this.userId = userId;
        this.isAdult = isAdult;
        this.isAdmin = isAdmin;
    }

    /**
     * Returns user id.
     * @return String
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns if user is adult.
     * @return boolean Returns true if user is an adult.
     */
    public boolean isAdult() {
        return isAdult;
    }

    /**
     * Returns if user is admin.
     * @return boolean Returns true if user is an admin.
     */
    public boolean isAdmin() {
        return isAdmin;
    }
}
