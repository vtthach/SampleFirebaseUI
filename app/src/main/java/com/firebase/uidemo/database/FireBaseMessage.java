package com.firebase.uidemo.database;

import com.google.gson.annotations.SerializedName;

public class FireBaseMessage {
    @SerializedName("create_timestamp")
    public double create_timestamp;
    @SerializedName("data")
    public String data;
    @SerializedName("notification")
    public boolean notification;
    @SerializedName("read")
    public boolean read;
    @SerializedName("user_firebase_id")
    public String user_firebase_id;

    public FireBaseMessage() {
        // Needed for firebase
    }

    public FireBaseMessage(String name, String message, String uid) {
        create_timestamp = System.currentTimeMillis()/1000d;
        data = message;
        notification = false;
        read = false;
        user_firebase_id = uid;
    }
}
