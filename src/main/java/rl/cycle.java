package rl;

import arc.util.Log;
import arc.util.Time;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class cycle extends Thread {
    private Thread MainT;
    private HashMap<Integer, Integer> spawnTimer = new HashMap<Integer, Integer>();

    public cycle(Thread main) {
        MainT = main;
    }

    public void run() {
        Log.info("rl cycle started - Waiting 15 Seconds");
        Main.cycle = Thread.currentThread();
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (Exception ignored) {
        }
        Log.info("rl cycle running");
        //Var
        long nexSecond = Time.millis()+1000;
        //
        while (MainT.isAlive()) {
            try {
                try {
                    TimeUnit.MILLISECONDS.sleep(nexSecond - Time.millis());
                } catch (Exception ignored) {
                }
                if (Time.millis() >= nexSecond) {
                    nexSecond = Time.millis() + 1000;
                    //run
                    Main.packet38RL.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
