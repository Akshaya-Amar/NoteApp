package com.example.roomdatabasemvvm.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.roomdatabasemvvm.Note;
import com.example.roomdatabasemvvm.NoteDao;
import com.example.roomdatabasemvvm.NoteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {
    private final NoteDao noteDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
    }

    public void insert(Note note) {
        executorService.execute(() -> noteDao.insert(note));
    }

    public void update(Note note) {
        executorService.execute(() -> noteDao.update(note));
    }

    public void delete(Note note) {
        executorService.execute(() -> noteDao.delete(note));
    }

    public void deleteAllNotes() {
        noteDao.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }
}