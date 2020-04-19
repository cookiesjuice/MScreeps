package com.cookiesjuice.mscreeps;

import com.cookiesjuice.mscreeps.MainActivity;

public class ConsoleThread implements Runnable{
    MainActivity activity;
    public ConsoleThread(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public void run() {

    }
}
