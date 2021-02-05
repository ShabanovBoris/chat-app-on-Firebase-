package com.example.chatapp;

public class ChatMessage {

    private String text;
    private String name;
    private String imageURL;
    private String sender;
    private String recipient;
    private boolean isMine;



    public  ChatMessage(){

    }

    public ChatMessage(String text, String name, String imageURL, String sender, String recipient, boolean isMine) {
        this.text = text;
        this.name = name;
        this.imageURL = imageURL;
        this.sender = sender;
        this.recipient = recipient;
        this.isMine = isMine;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
