package com.epicodus.theinterview.models;

import org.parceler.Parcel;

/**
 * Created by joannaanderson on 12/19/16.
 */

@Parcel
public class Feedback {
    String textBody;
    String pushId;
    String userId;

    public Feedback(){}

    public Feedback(String textBody, String userId){
        this.textBody = textBody;
        this.userId = userId;
    }

    public String getTextBody() {
        return textBody;
    }

    public String getUserId() {
        return userId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
