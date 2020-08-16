package rl;

import arc.util.Log;
import arc.util.Time;
import mindustry.gen.Call;

import java.util.concurrent.TimeUnit;

public class cycle extends Thread {
    private Thread MainT;

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
                    if (!Main.warn.equals("")) Call.sendMessage(Main.warn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
