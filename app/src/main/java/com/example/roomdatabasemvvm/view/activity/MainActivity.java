package com.example.roomdatabasemvvm.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.roomdatabasemvvm.R;
import com.example.roomdatabasemvvm.data.database.Note;
import com.example.roomdatabasemvvm.databinding.ActivityMainBinding;
import com.example.roomdatabasemvvm.view.adapter.NoteAdapter;
import com.example.roomdatabasemvvm.viewmodel.NoteViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NoteViewModel noteViewModel;
    private RecyclerView recyclerView;
    private View rootElement;
    private List<Note> noteList;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        rootElement = binding.getRoot(); // to get the root element of the layout for usage in Snack bar

        // set up view model
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        binding.setNoteViewModel(noteViewModel);
        binding.setLifecycleOwner(this);

        // set up recyclerview
        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteAdapter(this);
        recyclerView.setAdapter(adapter);

        // enable swipe function on items to delete note
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        // observe live data from view model
        noteViewModel.getAllNotes().observe(this, notes -> {
            noteList = notes;
            adapter.setNoteList(notes);
        });

        binding.searchTextInputLayout.setEndIconOnClickListener(v -> clearSearchText());

        binding.searchTextInputEditText.addTextChangedListener(createTextWatcher());

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
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
    };

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable enteredText) {
                filter(enteredText.toString());
            }
        };
    }

    private void clearSearchText() {
        binding.searchTextInputEditText.setText("");
    }

    private void filter(String enteredText) {

        enteredText = enteredText.toLowerCase(Locale.getDefault());

        ArrayList<Note> filteredList = new ArrayList<>();
        for (Note note : noteList) {
            String title = note.getTitle().toLowerCase(Locale.getDefault());
            String description = note.getDescription().toLowerCase(Locale.getDefault());
            if (title.contains(enteredText) || description.contains(enteredText)) {
                filteredList.add(note);
            }
        }

        adapter.filterList(filteredList);
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

        if (binding.searchTextInputEditText.getText().toString().isEmpty()) {
            binding.searchTextInputEditText.clearFocus();
        }

        new Handler().postDelayed(() -> {
            if (recyclerView != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
                recyclerView.scrollToPosition(0);
            }
        }, 50);
    }
}