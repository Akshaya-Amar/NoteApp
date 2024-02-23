package com.example.roomdatabasemvvm.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomdatabasemvvm.R;
import com.example.roomdatabasemvvm.databinding.ActivityNoteBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private ActivityNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.doneImageView.setOnClickListener(v -> {
            saveNote();
        });

        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_ID")) { // for note editing purpose
            binding.titleEditText.setText(intent.getStringExtra("EXTRA_TITLE"));
            binding.descriptionEditText.setText(intent.getStringExtra("EXTRA_DESCRIPTION"));
            binding.addEditTextView.setText(getResources().getString(R.string.edit_note));
        }
    }

    private void saveNote() {

        String title = binding.titleEditText.getText().toString();
        String description = binding.descriptionEditText.getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Title/Description cannot be empty", Snackbar.LENGTH_LONG).show();
            return;
        }

        Intent data = new Intent();

        data.putExtra("EXTRA_TITLE", title);
        data.putExtra("EXTRA_DESCRIPTION", description);
        data.putExtra("EXTRA_DATE_TIME", getCurrentDateTime());

        int id = getIntent().getIntExtra("EXTRA_ID", -1);
        if (id != -1) {
            data.putExtra("EXTRA_ID", id); // condition to put id only when there is a need to update note
        }

        setResult(AppCompatActivity.RESULT_OK, data);
        finish();
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd, h:mm:ss a", Locale.getDefault());
        return inputFormat.format(new Date());
    }
}