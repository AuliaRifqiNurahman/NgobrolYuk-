package com.example.aulia_rifqi.chatkuy;

public class Messages {

    private String message, type, from;
    private long time;
    private boolean seen;

    public Messages (String message, String type, String from, long time, boolean seen){
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;
    }

    public Messages(){
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public long getTime() {

        return time;

    }

    public void setTime(long time) {

        this.time = time;

    }

    public boolean isSeen() {

        return seen;

    }

    public void setSeen(boolean seen) {

        this.seen = seen;

    }

}
