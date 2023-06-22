package com.example.voicerecorder;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements Timer.OnTimerTickListener {
    public static final int REQUEST_CODE = 200;
    private final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
   private boolean permissionGranted = false;
   private ImageButton btnRecord,btnDelete,btnDone;
   private TextView tvTimer;
   private MediaRecorder recorder;
   private String dirPath = "";
   private String fileName = "";
   private boolean isRecording = false;
   private boolean isPause = false;
   private Timer timer ;
   private Vibrator vibrator;
   private WaveformView waveformView;
   private ArrayList<Float> amplitudes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;

        if(!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE);
        }

        timer = new Timer(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnDelete.setEnabled(false);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPause){
                    resumeRecording();
                }else if(isRecording){
                    pauseRecording();
                }else{
                    startRecording();
                }
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));


            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                Toast.makeText(MainActivity.this, "Recording Completed.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initViews() {
        btnRecord = findViewById(R.id.btnRecord);
        tvTimer = findViewById(R.id.tvTimer);
        waveformView = findViewById(R.id.waveformView);
        btnDelete = findViewById(R.id.btnDelete);
//        btnDone = findViewById(R.id.btnDone);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }


    }

    private void startRecording(){
        if(!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE);

        }

        //TODO - Start recording here.
        recorder = new MediaRecorder();
        dirPath = getExternalCacheDir() != null ? getExternalCacheDir().getAbsolutePath() + "/" : "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.DD_hh.mm.ss");
        String date = simpleDateFormat.format(new Date());
        fileName = "audio " + date;
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(dirPath+fileName+".mp3");

        try {
            recorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        recorder.start();

        btnRecord.setImageResource(R.drawable.ic_pause);
        isRecording = true;
        isPause = false;

        timer.start();
        btnDelete.setEnabled(true);
        btnDelete.setImageResource(R.drawable.ic_delete_disabled);





    }

    private void pauseRecording() {
        recorder.pause();
        isPause= true;
        btnRecord.setImageResource(R.drawable.ic_record);
        timer.pause();
    }

    private void resumeRecording() {
        recorder.resume();
        isPause = false;
        btnRecord.setImageResource(R.drawable.ic_pause);
        timer.start();
    }

    private void stopRecording(){
        timer.stop();
        recorder.stop();
        recorder.release();
        isPause = false;
        isRecording = false;
        btnDelete.setEnabled(false);
        btnDelete.setImageResource(R.drawable.ic_delete);
        btnRecord.setImageResource(R.drawable.ic_record);
        tvTimer.setText("00:00:00");
        amplitudes = waveformView.clear();
    }


    @Override
    public void onTimerTick(String duration) {
        tvTimer.setText(duration);
        waveformView.addAmplitude((float)recorder.getMaxAmplitude());

    }
}