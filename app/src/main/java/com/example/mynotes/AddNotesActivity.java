package com.example.mynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AddNotesActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "NotePrefs";
    private static final String KEY_NOTE_COUNT = "NoteCount";
    private Button saveNotes;
    private LinearLayout NotesContainer;
    private List<Notes> notesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotes);
        saveNotes = findViewById(R.id.btn_SaveNotes);
        NotesContainer = findViewById(R.id.NotesContainer);
        notesList = new ArrayList<>();

        saveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveNote();
            }
        });
        loadNotesFromPreferences();
        displayNotes();

    }

    private void displayNotes() {
        for(Notes notes:notesList){
            createNoteView(notes);
        }
    }

    private void loadNotesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        int count = sharedPreferences.getInt(KEY_NOTE_COUNT,0);

        for(int i=0;i<count;i++)
        {
            String title = sharedPreferences.getString("note_title_" + i,"");
            String content = sharedPreferences.getString("note_content_" + i,"");

            Notes notes = new Notes();
            notes.setTitle(title);
            notes.setContent(content);
            notesList.add(notes);
        }
    }

    private void SaveNote(){

        EditText notesTitle = findViewById(R.id.NotesTitle);;
        EditText notesContent = findViewById(R.id.NoteContent);
        String title = notesTitle.getText().toString();
        String content = notesContent.getText().toString();

        if(!title.isEmpty() && !content.isEmpty()){
            Notes notes = new Notes();
            notes.setTitle(title);
            notes.setContent(content);
            notesList.add(notes);
            saveNotesToPreferences();
            createNoteView(notes);
            clearInputFields();
        }
    }

    private void clearInputFields() {
        EditText notesTitle = findViewById(R.id.NotesTitle);;
        EditText notesContent = findViewById(R.id.NoteContent);

        notesTitle.getText().clear();
        notesContent.getText().clear();
    }

    private void createNoteView(final Notes notes) {
        View noteview = getLayoutInflater().inflate(R.layout.note_item,null);
        TextView noteTitle = noteview.findViewById(R.id.NotesTitle);
        TextView noteContent = noteview.findViewById(R.id.NotesContent);
        ImageButton editButton = noteview.findViewById(R.id.editButton);

        noteTitle.setText(notes.getTitle());
        noteContent.setText(notes.getContent());


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(notes);
            }
        });

        noteview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteDialog(notes);
                return true;
            }
        });
        NotesContainer.addView(noteview);
    }

    private void showDeleteDialog(Notes notes) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this note");
        builder.setMessage("Are you sure you want to delete this note ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNotesAndRefresh(notes);
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

    private void deleteNotesAndRefresh(Notes notes) {
        notesList.remove(notes);
        saveNotesToPreferences();
        refreshNoteView();
    }

    private void showEditDialog(final Notes notes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");


        // Inflate the layout for editing notes
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_note, null);
        builder.setView(dialogView);

        EditText editTitle = dialogView.findViewById(R.id.edit_note_title);
        EditText editContent = dialogView.findViewById(R.id.edit_note_content);

        // Set the current title and content of the note
        editTitle.setText(notes.getTitle());
        editContent.setText(notes.getContent());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Update the note with the new title and content
                notes.setTitle(editTitle.getText().toString());
                notes.setContent(editContent.getText().toString());
                saveNotesToPreferences();
                refreshNoteView();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void refreshNoteView() {
        NotesContainer.removeAllViews();
        displayNotes();
    }

    private void saveNotesToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NOTE_COUNT,notesList.size());
        for(int i=0;i<notesList.size();i++)
        {
            Notes notes = notesList.get(i);
            editor.putString("note_title_" + i,notes.getTitle());
            editor.putString("note_content_" +i ,notes.getContent());
        }
        editor.apply();
    }
}
