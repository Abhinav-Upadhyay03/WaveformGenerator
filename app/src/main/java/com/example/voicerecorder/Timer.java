package com.example.voicerecorder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.logging.LogRecord;

public class Timer{


    
    private OnTimerTickListener listener;

    public Timer(OnTimerTickListener listener){
        this.listener = listener;
    }

    public interface OnTimerTickListener {
        void onTimerTick(String duration);
    }


    private long duration = 0L;
    private long delay = 100L;


    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            duration+=delay;
            handler.postDelayed(runnable, delay);

            listener.onTimerTick(format());
        }
    };

    void start(){
        handler.postDelayed(runnable, delay);
    }

    void pause(){
        handler.removeCallbacks(runnable);
    }

    void stop(){
        handler.removeCallbacks(runnable);
        duration = 0L;
    }

    String format(){
        long millis = duration%1000;
        long seconds = (duration/1000)%60;
        long minutes = (duration/ (1000*60))%60;
        long hours = (duration/(1000*60*60));
        String formatted;
        if(hours>0){
            formatted = String.format("%02d:%02d:%02d:%02d", hours,minutes,seconds,millis/10);
        }else{
            formatted = String.format("%02d:%02d:%02d",minutes,seconds,millis/10);
        }
        return formatted;

    }




}
