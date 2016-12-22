package com.epicodus.theinterview.models;

import org.parceler.Parcel;

/**
 * Created by joannaanderson on 12/19/16.
 */

@Parcel
public class Chat {
    String hiringManager;
    String interviewee;
    boolean active;
    String hiringManagerChatId;
    String intervieweeChatId;

    public Chat(){}

    public Chat(String hiringManager, String interviewee){
        this.hiringManager = hiringManager;
        this.interviewee = interviewee;
        this.active = true;

    }

    public String getHiringManager() {
        return hiringManager;
    }

    public String getInterviewee() {
        return interviewee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getHiringManagerChatId() {
        return hiringManagerChatId;
    }

    public void setHiringManagerChatId(String hiringManagerChatId) {
        this.hiringManagerChatId = hiringManagerChatId;
    }

    public String getIntervieweeChatId() {
        return intervieweeChatId;
    }

    public void setIntervieweeChatId(String intervieweeChatId) {
        this.intervieweeChatId = intervieweeChatId;
    }
}

