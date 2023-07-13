package com.example.voicerecorder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "audioRecords")
public class AudioRecord {
    private String fileName;
    private String filePath;
    private Long timeStamp;
    private String duration;
    private String ampsPath;

    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    @Ignore
    private boolean isChecked = false;

    public AudioRecord(String fileName, String filePath, Long timeStamp, String duration, String ampsPath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.timeStamp = timeStamp;
        this.duration = duration;
        this.ampsPath = ampsPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAmpsPath() {
        return ampsPath;
    }

    public void setAmpsPath(String ampsPath) {
        this.ampsPath = ampsPath;
    }
}
