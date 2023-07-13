package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
        //TODO
        Toast.makeText(this, "simple click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClickListener(int position) {
        //TODO
        Toast.makeText(this, "long click", Toast.LENGTH_SHORT).show();

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