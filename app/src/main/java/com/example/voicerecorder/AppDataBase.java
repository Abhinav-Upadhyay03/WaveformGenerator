package com.example.voicerecorder;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AudioRecord.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    abstract AudioRecordData audioRecordDao();
}
