package com.shaubert.liftago.util;

import android.os.Handler;
import android.os.SystemClock;

public class ActionWithThrottle {

    private Runnable task;
    private Handler handler;
    private long throttle;
    private long lastExecutionTime;

    private Runnable forceTask = new Runnable() {
        @Override
        public void run() {
            force();
        }
    };

    public ActionWithThrottle(long throttle, Runnable task) {
        this(new Handler(), throttle, task);
    }

    public ActionWithThrottle(Handler handler, long throttle, Runnable task) {
        this.handler = handler;
        this.throttle = throttle;
        this.task = task;
    }

    public void schedule() {
        long time = SystemClock.uptimeMillis();
        long diff = time - lastExecutionTime;
        if (diff >= throttle) {
            force();
        } else {
            handler.removeCallbacks(forceTask);
            handler.postDelayed(forceTask, throttle - diff);
        }
    }

    public void force() {
        lastExecutionTime = SystemClock.uptimeMillis();
        task.run();
    }
}
