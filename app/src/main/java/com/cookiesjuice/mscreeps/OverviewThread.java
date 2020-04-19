package com.cookiesjuice.mscreeps;

import java.util.Observable;

public class OverviewThread extends Thread implements Runnable {
    private final int SLEEP_DURATION = 10000; // refreshes every 10 seconds

    private HttpClient client;
    private boolean closing;
    private OverviewRunnable target;
    public OverviewThread(MainActivity activity){
        client = new HttpClient(activity);
        closing = false;
        target = new OverviewRunnable();

    }

    @Override
    public void run() {
        target.run();
    }

    public OverviewRunnable getTarget(){
        return target;
    }

    private class OverviewRunnable extends Observable implements Runnable{

        @Override
        public void run() {
            while (!closing){
                try {
                    String s = client.readMemorySegment();
                    System.out.println("Running, s = " + s);
                    setChanged();
                    notifyObservers(s);
                    sleep(SLEEP_DURATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close(){
        closing = true;
    }

    @Override
    public void finalize(){
        closing = true;
    }
}

