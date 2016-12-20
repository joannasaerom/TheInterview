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
    String feedbackGivenById;
    long timestamp;

    public Feedback(){}

    public Feedback(String textBody, String userId, String feedbackGivenById, long timestamp){
        this.textBody = textBody;
        this.userId = userId;
        this.feedbackGivenById = feedbackGivenById;
        this.timestamp = timestamp;
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

    public String getFeedbackGivenById() {
        return feedbackGivenById;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
