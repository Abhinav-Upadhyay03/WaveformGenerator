package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AudioPlayerActivity extends AppCompatActivity {

    private MediaPlayer mPLayer;
    private ImageButton btnPlay;
    private SeekBar seekBar;

    private Runnable runnable;
    private Handler handler;
    private long delay = 100L;
    private MaterialToolbar toolbar;
    private TextView tvFilename;
    private TextView tvTrackProgress;
    private TextView tvTrackDuration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        initViews();

        String filePath = getIntent().getStringExtra("filePath");
        String fileName = getIntent().getStringExtra("fileName");

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        }
        tvFilename.setText(fileName);



        mPLayer = new MediaPlayer();
        try {
            mPLayer.setDataSource(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            mPLayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tvTrackDuration.setText((dateFormat(mPLayer.getDuration())));





        handler =  new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mPLayer.getCurrentPosition());
                tvTrackProgress.setText(dateFormat(mPLayer.getCurrentPosition()));
                handler.postDelayed(runnable,delay);
            }
        };

        seekBar.setMax(mPLayer.getDuration());
        playPausePlayer();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPausePlayer();
            }
        });

        mPLayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_circle, null));
                handler.removeCallbacks(runnable);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mPLayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void playPausePlayer(){
        if(!mPLayer.isPlaying()){
            mPLayer.start();
            btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_circle, null));
            handler.postDelayed(runnable,0);

        }else{
            mPLayer.pause();
            btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_circle, null));
            handler.removeCallbacks(runnable);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPLayer.stop();
        mPLayer.release();
        handler.removeCallbacks(runnable);
    }

    public void initViews(){
        btnPlay = findViewById(R.id.btnPlay);
        seekBar = findViewById(R.id.seekbar);
        toolbar  = findViewById(R.id.toolbar);
        tvFilename = findViewById(R.id.tvFilename);
        tvTrackDuration = findViewById(R.id.tvTrackDuration);
        tvTrackProgress = findViewById(R.id.tvTrackProgress);

    }

    private String dateFormat(int duration){
        int d = duration/1000;
        int s = d%60;
        int m = (d/60 % 60);
        int h = (int)((d - (m * 60)) / 360);
        NumberFormat f = new DecimalFormat("00");
        String str = m + ":" + f.format(s);
        if(h>0){
            str = h + ":" + str;
        }
        return str;
    }
}