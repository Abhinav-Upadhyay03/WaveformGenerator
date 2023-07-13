package com.example.voicerecorder;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;


/**
 * DAO - Data Access Object
 * The interface contains several methods
 * annotated with Room annotations to perform database operations on the "audioRecords" table.
 **/

@Dao
public interface AudioRecordData {
    @Query("SELECT * FROM audioRecords")
    List<AudioRecord> getAll();

    @Insert
    void insert(AudioRecord audioRecord);

    @Delete
    void delete(AudioRecord audioRecord);

    @Delete
    void delete(ArrayList<AudioRecord> audioRecords);

    @Update
    void update(AudioRecord audioRecord);


}
