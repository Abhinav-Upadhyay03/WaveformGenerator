package com.example.voicerecorder;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.CoroutineExceptionHandler;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;


public class MainActivity extends AppCompatActivity implements Timer.OnTimerTickListener {
    public static final int REQUEST_CODE = 200;
    private final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
   private boolean permissionGranted = false;
   private ImageButton btnRecord,btnDelete,btnDone,btnList;
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
   private LinearLayout bottomSheet;

   private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
   private View bottomSheetBG;
   private EditText filenameInput;
   private Button btnCancel, btnOK;

   private AppDataBase db;
   private String duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
        initViews();

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;

        if(!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE);
        }

        db = Room.databaseBuilder(this, AppDataBase.class, "audioRecords")
                .build();


        timer = new Timer(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnDelete.setEnabled(false);


        //*******************************Bottom Sheet**********************************

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);




                //***************** Click Listeners *****************************
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
                File file = new File(dirPath+fileName+".mp3");
                file.delete();
                Toast.makeText(MainActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();

            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GalleryActivity.class);
                startActivity(intent);


            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();


                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBG.setVisibility(View.VISIBLE);
                filenameInput.setText(fileName);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(dirPath+fileName+".mp3");
                file.delete();
                Toast.makeText(MainActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                save();
                Toast.makeText(MainActivity.this, "Record Saved", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(dirPath+fileName+".mp3");
                file.delete();
                dismiss();
            }
        });

        btnDelete.setClickable(false);



    }

    private void dismiss(){
        bottomSheetBG.setVisibility(View.GONE);

        hideKeyboard(filenameInput);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }, 100);
    }
    private void save(){
         String newFileName = filenameInput.getText().toString();
         if(!newFileName.equals(fileName)){
             File file = new File(dirPath+fileName+".mp3");
             File newFile = new File(dirPath+newFileName+".mp3");
             file.renameTo(newFile);

         }

         String filePath = dirPath+newFileName+".mp3";
         long timestamp = System.currentTimeMillis();
         String ampsPath = dirPath+newFileName;

        try {
            FileOutputStream fos = new FileOutputStream(ampsPath);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(amplitudes);
            fos.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioRecord record = new AudioRecord(newFileName, filePath, timestamp, duration, ampsPath);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.audioRecordDao().insert(record);
            }
        });

        executor.shutdown();








    }

    private void initViews() {
        btnRecord = findViewById(R.id.btnRecord);
        tvTimer = findViewById(R.id.tvTimer);
        waveformView = findViewById(R.id.waveformView);
        btnDelete = findViewById(R.id.btnDelete);
        btnDone = findViewById(R.id.btnDone);
        btnList = findViewById(R.id.btnList);
        bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBG = findViewById(R.id.bottomSheetBG);
        filenameInput = findViewById(R.id.filenameInput);
        btnCancel = findViewById(R.id.btnCancel);
        btnOK = findViewById(R.id.btnOk);
    }

    private void hideKeyboard(View view){
        InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
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

        //TODO: Save to device storage.

        /**     We need to set the output file location before preparing the recorder
                i.e recorder.prepare()
         **/

        recorder = new MediaRecorder();
//        dirPath = getApplicationContext().getFilesDir().getAbsolutePath();              /* For internal storage */
        dirPath = getExternalCacheDir() != null ? getExternalCacheDir().getAbsolutePath() + "/" : "";     /* For external storage in cache */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.DD_hh.mm.ss");
        String date = simpleDateFormat.format(new Date());
        fileName = "audio " + date;
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(dirPath+fileName+".mp3");

        //******************************************************************************************





//        String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_directory";
//        File directory = new File(directoryPath);
//        boolean isDirectoryCreated = directory.mkdirs();
//
//        if (isDirectoryCreated) {
//            String fileName = "audio_2023.07.196_10.55.48.mp3";
//            String filePath = directory.getAbsolutePath() + File.separator + fileName;
//            recorder.setOutputFile(filePath);
//        }


        //******************************************************************************************


//        String file_folder="/abhinav/";
//
//        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//        File file = new File(file_path,file_folder);
//        recorder.setAudioChannels(1);
//        recorder.setAudioSamplingRate(8000);
//        recorder.setAudioEncodingBitRate(44100);
//
//
//        if (!file.exists()){
//            file.mkdirs();
//        }
//        recorder.setOutputFile(file_path+fileName+".mp3");
//        System.out.println(
//                dirPath+fileName+".mp3"
//        );

//        recorder.setOutputFile(file.getAbsolutePath()+"/"+"_"+date+".amr");



        //******************************************************************************************

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
        btnDelete.setClickable(true);
        btnDelete.setImageResource(R.drawable.ic_delete_disabled);

        btnList.setVisibility((View.GONE));
        btnDone.setVisibility((View.VISIBLE));




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
        btnList.setVisibility(View.VISIBLE);
        btnDone.setVisibility((View.GONE));
        btnDelete.setClickable(false);
        btnDelete.setImageResource(R.drawable.ic_delete);
        btnRecord.setImageResource(R.drawable.ic_record);
        tvTimer.setText("00:00:00");
        amplitudes = waveformView.clear();






    }


    @Override
    public void onTimerTick(String duration) {
        tvTimer.setText(duration);

        this.duration = duration.substring(0, duration.length() - 3);

        waveformView.addAmplitude((float)recorder.getMaxAmplitude());

    }
}