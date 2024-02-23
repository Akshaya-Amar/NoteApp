package com.example.roomdatabasemvvm.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title_column")
    private final String title;

    private final String description;
    @ColumnInfo(name = "current_date_time")
    private final String currentDateTime;

    public Note(String title, String description, String currentDateTime) {
        this.title = title;
        this.description = description;
        this.currentDateTime = currentDateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }
}