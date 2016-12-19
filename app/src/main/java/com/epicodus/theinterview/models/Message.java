package com.epicodus.theinterview.models;

import org.parceler.Parcel;

/**
 * Created by joannaanderson on 12/19/16.
 */

@Parcel
public class Message {
    String textBody;
    long timestamp;
    String pushId;
    String userId;
    String chatId;

    public Message(){}

    public Message(String textBody, long timestamp, String userId, String chatId){
        this.textBody = textBody;
        this.timestamp = timestamp;
        this.userId = userId;
        this.chatId = chatId;
    }

    public String getTextBody() {
        return textBody;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
