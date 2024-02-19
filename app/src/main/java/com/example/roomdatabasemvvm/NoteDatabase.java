package com.example.roomdatabasemvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 2)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Application application) {

        if (instance == null) {
            instance = Room.databaseBuilder(application.getApplicationContext(), NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new PopulateData(instance));
        }
    };

    private static class PopulateData implements Runnable {

        private final NoteDao noteDao;

        public PopulateData(NoteDatabase noteDatabase) {
            this.noteDao = noteDatabase.noteDao();
        }

        @Override
        public void run() {
            noteDao.insert(new Note("Title 1", "Description 1"));
            noteDao.insert(new Note("Title 2", "Description 2"));
            noteDao.insert(new Note("Title 3", "Description 3"));
        }
    }
}