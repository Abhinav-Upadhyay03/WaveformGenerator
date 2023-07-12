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
    private Paint axisPaint = new Paint();
    private Paint textPaint = new Paint();
    private float axisPadding = 50f;
    private float textSize = 17f;


    private List<Float> amplitudes = new ArrayList<>();
    private List<RectF> spikes = new ArrayList<>();

    private float radius = 6f;
    private float w = 9f;
    private float d = 6f;

    private float sw = 0f;
    private float sh = 1000f;
    private int maxSpikes = 0;

    void init(){

        axisPaint.setColor(Color.BLACK);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(2);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        paint.setColor(Color.rgb(244,81,30));
        sw = (float)(getResources().getDisplayMetrics().widthPixels);
        maxSpikes = (int)(sw/(w+d));
     }

    void addAmplitude(Float amp){


        float norm = (float)Math.min((int)(amp/7),1000) ;
        amplitudes.add(norm);
        spikes.clear();

        List<Float> amps = amplitudes.subList(Math.max(amplitudes.size() - maxSpikes, 0), amplitudes.size());



        for(int i = 0;i<amps.size();i++ ){
             float left =i*(w+d)+axisPadding;
             float top = sh/2 - amps.get(i)/2;
             float right = left+w;
             float bottom = top + amps.get(i);
             spikes.add(new RectF(left,top,right,bottom));
         }



         invalidate();

    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float width = getWidth();
        float height = getHeight();

        // Draw x-axis
        if (axisPaint != null) {
            canvas.drawLine(5, height / 2, width-5, height / 2, axisPaint);

            // Draw x-axis label
            String xAxisLabel = "Time";
            float labelX = width - axisPadding - 25;
            float labelY = height / 2 - axisPadding / 2;
            canvas.drawText(xAxisLabel, labelX, labelY, textPaint);
        }

        // Draw y-axis
        if (axisPaint != null) {
            canvas.drawLine(axisPadding, 25, axisPadding, height, axisPaint);

            // Draw y-axis label
            String yAxisLabel = "Amplitude";
            float labelX = axisPadding / 2;
            float labelY = 15;
            canvas.drawText(yAxisLabel, labelX, labelY, textPaint);
        }


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
