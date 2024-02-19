package com.example.roomdatabasemvvm.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdatabasemvvm.data.database.Note;
import com.example.roomdatabasemvvm.R;
import com.example.roomdatabasemvvm.databinding.ActivityMainBinding;
import com.example.roomdatabasemvvm.view.adapter.NoteAdapter;
import com.example.roomdatabasemvvm.viewmodel.NoteViewModel;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private RecyclerView recyclerView;
    private View rootElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        rootElement = binding.getRoot();

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        binding.setNoteViewModel(noteViewModel);
        binding.setLifecycleOwner(this);

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NoteAdapter adapter = new NoteAdapter(this);
        recyclerView.setAdapter(adapter);

        noteViewModel.getAllNotes().observe(this, adapter::setNoteList);

        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("EXTRA_ID", note.getId());
            intent.putExtra("EXTRA_TITLE", note.getTitle());
            intent.putExtra("EXTRA_DESCRIPTION", note.getDescription());
            activityResultLauncherUpdateNote.launch(intent);
        });

        binding.newNoteFloatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            activityResultLauncherAddNote.launch(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = adapter.getNoteAt(position);
                noteViewModel.delete(note);
                Snackbar.make(rootElement, "Note deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> noteViewModel.insert(note))
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncherAddNote = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {

                        Intent data = result.getData();

                        if (data != null) {

                            String title = data.getStringExtra("EXTRA_TITLE");
                            String description = data.getStringExtra("EXTRA_DESCRIPTION");

                            Note note = new Note(title, description);
                            noteViewModel.insert(note);
                            Snackbar.make(rootElement, "Note saved", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(rootElement, "Note not saved", Snackbar.LENGTH_LONG).show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> activityResultLauncherUpdateNote = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {

                        Intent data = result.getData();

                        if (data != null) {

                            int id = data.getIntExtra("EXTRA_ID", -1);

                            if (id != -1) { // not necessary

                                String title = data.getStringExtra("EXTRA_TITLE");
                                String description = data.getStringExtra("EXTRA_DESCRIPTION");

                                Note note = new Note(title, description);
                                note.setId(id);
                                noteViewModel.update(note);

                                Snackbar.make(rootElement, "Note updated", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(rootElement, "Unable to update note", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Snackbar.make(rootElement, "Note not updated", Snackbar.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
                    recyclerView.scrollToPosition(0);
                }
            }
        }, 50);
    }

}