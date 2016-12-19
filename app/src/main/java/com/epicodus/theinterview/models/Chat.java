package com.epicodus.theinterview.models;

import org.parceler.Parcel;

/**
 * Created by joannaanderson on 12/19/16.
 */

@Parcel
public class Chat {
    String hiringManager;
    String interviewee;
    String pushId;

    public Chat(){}

    public Chat(String hiringManager, String interviewee){
        this.hiringManager = hiringManager;
        this.interviewee = interviewee;

    }

    public String getHiringManager() {
        return hiringManager;
    }

    public String getInterviewee() {
        return interviewee;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}

