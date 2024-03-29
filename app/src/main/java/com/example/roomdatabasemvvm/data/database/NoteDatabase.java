package com.example.roomdatabasemvvm.data.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.roomdatabasemvvm.view.activity.NoteActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Application application) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                            application.getApplicationContext(),
                            NoteDatabase.class,
                            "note_database")
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
            noteDao.insert(new Note("Edit Title", "Edit Description", NoteActivity.getCurrentDateTime()));
        }
    }
}