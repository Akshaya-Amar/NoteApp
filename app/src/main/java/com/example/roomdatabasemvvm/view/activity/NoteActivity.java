package com.example.roomdatabasemvvm.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomdatabasemvvm.R;

public class NoteActivity extends AppCompatActivity {

    private EditText titleET;
    private EditText descriptionET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        titleET = findViewById(R.id.title_edit_text);
        descriptionET = findViewById(R.id.description_edit_text);
        TextView addEditTextView = findViewById(R.id.add_edit_text_view);

        findViewById(R.id.done_image_view).setOnClickListener(new View.OnClickListener() {
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
            addEditTextView.setText(getResources().getString(R.string.edit_note));
        }
    }

    private void saveNote() {

        String title = titleET.getText().toString();
        String description = descriptionET.getText().toString();

        Intent data = new Intent();

        int id = getIntent().getIntExtra("EXTRA_ID", -1);
        if (id != -1) {
            data.putExtra("EXTRA_ID", id); // condition to put id only when there is a need to update note
        }

        data.putExtra("EXTRA_TITLE", title);
        data.putExtra("EXTRA_DESCRIPTION", description);

        setResult(AppCompatActivity.RESULT_OK, data);
    }
}