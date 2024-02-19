package com.example.roomdatabasemvvm.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roomdatabasemvvm.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class NoteActivity extends AppCompatActivity {

    private EditText titleET;
    private EditText descriptionET;
    private TextView addEditTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        titleET = findViewById(R.id.title_edit_text);
        descriptionET = findViewById(R.id.description_edit_text);
        addEditTextView = findViewById(R.id.add_edit_text_view);
        imageView = findViewById(R.id.done_image_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_ID")) { // for note edit purpose
            titleET.setText(intent.getStringExtra("EXTRA_TITLE"));
            descriptionET.setText(intent.getStringExtra("EXTRA_DESCRIPTION"));
            addEditTextView.setText("Edit Note");
        } else {
            addEditTextView.setText("Add Note");
        }
    }

    private void saveNote() {

        String title = titleET.getText().toString();
        String description = descriptionET.getText().toString();

        Intent data = new Intent();
        data.putExtra("EXTRA_TITLE", title);
        data.putExtra("EXTRA_DESCRIPTION", description);

        int id = getIntent().getIntExtra("EXTRA_ID", -1);
        if (id != -1) {
            data.putExtra("EXTRA_ID", id); // condition to put id only when there is a need to update note
        }

        setResult(AppCompatActivity.RESULT_OK, data);
    }
}