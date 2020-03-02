package com.ayanda.messenger;

public class Message {
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";
    private String body = null;
    private String userID = null;

    public String getUserId() {
        return userID;
    }

    public String getBody() {
        return body;
    }

    public void setUserId(String userId) {
         this.userID = userId;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
