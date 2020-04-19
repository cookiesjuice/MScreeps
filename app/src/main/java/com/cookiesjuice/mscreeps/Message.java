package com.cookiesjuice.mscreeps;

import androidx.annotation.NonNull;

public class Message {
    private String message;
    private boolean send;
    public Message(String message, boolean send){
        this.message = message;
        this.send = send;
    }

    @NonNull
    @Override
    public String toString() {
        String s;
        if(send){
            s = "> ";
        }else{
            s = "< ";
        }
        return s + message;
    }
}
