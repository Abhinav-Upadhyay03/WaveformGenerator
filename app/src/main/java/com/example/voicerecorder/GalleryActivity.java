package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlinx.coroutines.GlobalScope;

public class GalleryActivity extends AppCompatActivity implements OnItemClickListener{

    private ArrayList<AudioRecord> records;
    private Adapter mAdapter;
    private AppDataBase db;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initViews();

        records= new ArrayList<>();

         db = Room.databaseBuilder(
                 this,
                 AppDataBase.class,
                 "audioRecords"
         ).build();

        mAdapter = new Adapter(records,this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(GalleryActivity.this));

        fetchAll();


    }

    @Override
    public void onItemClickListener(int position) {
        AudioRecord audioRecord = records.get(position);
        Intent intent = new Intent(this, AudioPlayerActivity.class);

        intent.putExtra("filePath",audioRecord.getFilePath());
        intent.putExtra("fileName",audioRecord.getFileName());
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(int position) {
        //TODO : Can perform some task on long click if req. here. For now, keeping it same as the short click task.
        AudioRecord audioRecord = records.get(position);
        Intent intent = new Intent(this, AudioPlayerActivity.class);

        intent.putExtra("filePath",audioRecord.getFilePath());
        intent.putExtra("fileName",audioRecord.getFileName());
        startActivity(intent);

    }

    public void initViews(){
        recyclerView = findViewById(R.id.recyclerview);

    }
    private void fetchAll(){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                records.clear();
                List<AudioRecord> queryResult = db.audioRecordDao().getAll();
                records.addAll(queryResult);

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        executor.shutdown();
    }

}