package com.epicodus.theinterview.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joannaanderson on 12/19/16.
 */

@Parcel
public class Chat {
    String hiringManager;
    String interviewee;
    boolean active;
    int questionNumber;
    List<String> questions = new ArrayList<>();
    String hiringManagerChatId;
    boolean newMessage;
    String intervieweeChatId;

    public Chat(){}

    public Chat(String hiringManager, String interviewee){
        this.hiringManager = hiringManager;
        this.interviewee = interviewee;
        this.active = true;
        this.questionNumber = 0;
        this.newMessage = false;
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

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void addQuestion(String question) {
        this.questions.add(question);
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }
}

