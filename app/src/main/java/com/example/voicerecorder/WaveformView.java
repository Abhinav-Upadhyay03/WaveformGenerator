package com.example.voicerecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaveformView extends View {

    private Paint paint = new Paint();
    private List<Float> amplitudes = new ArrayList<>();
    private List<RectF> spikes = new ArrayList<>();

    private float radius = 6f;
    private float w = 9f;
    private float d = 6f;

    private float sw = 0f;
    private float sh = 400f;
    private int maxSpikes = 0;

    void init(){
        paint.setColor(Color.rgb(244,81,30));
        sw = (float)(getResources().getDisplayMetrics().widthPixels);
        maxSpikes = (int)(sw/(w+d));
     }

    void addAmplitude(Float amp){
        init();

        float norm = (float)Math.min((int)(amp/7),400) ;
        amplitudes.add(norm);
        spikes.clear();

        List<Float> amps = amplitudes.subList(Math.max(amplitudes.size() - maxSpikes, 0), amplitudes.size());



        for(int i = 0;i<amps.size();i++ ){
             float left =i*(w+d);
             float top = sh/2 - amps.get(i)/2;
             float right = left+w;
             float bottom = top + amps.get(i);
             spikes.add(new RectF(left,top,right,bottom));
         }



         invalidate();

    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for(RectF it: spikes){
            canvas.drawRoundRect(it,radius,radius,paint);
        }
    }

    public ArrayList<Float> clear(){
        ArrayList<Float> amps = new ArrayList<>(amplitudes);
       amplitudes.clear();
       spikes.clear();
       invalidate();
       return amps;
    }
}
