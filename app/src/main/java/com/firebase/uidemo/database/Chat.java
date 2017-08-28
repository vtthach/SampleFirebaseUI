package com.firebase.uidemo.database;

public class Chat {
    private String mName;
    private String mMessage;
    private String userId;

    public Chat() {
        // Needed for Firebase
    }

    public Chat(String name, String message, String uid) {
        mName = name;
        mMessage = message;
        userId = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getID() {
        return userId;
    }

    public void setID(String userId) {
        this.userId = userId;
    }
}
