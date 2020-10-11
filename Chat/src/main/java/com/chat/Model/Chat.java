package com.chat.Model;

public class Chat
{

    private String sender;
    private String receiver;
    private String message;
    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Chat(){}



    public Chat(String idSender, String idReciever , String msg, boolean seen)
    {
        this.sender = idSender;
        this.receiver = idReciever;
        this.message = msg;
        this.seen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
