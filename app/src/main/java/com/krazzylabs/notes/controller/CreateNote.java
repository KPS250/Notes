package com.krazzylabs.notes.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.model.Note;

public class CreateNote extends AppCompatActivity {

    EditText editText_title, editText_body;
    TextView textView_lastUpdate;
    String title, body, lastUpdate;

    FirebaseDatabase database;
    DatabaseReference myref;

    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_body = (EditText) findViewById(R.id.editText_body);
        textView_lastUpdate = (TextView) findViewById(R.id.textView_lastUpdate);

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("notes");

        try{
            Intent intent = getIntent();
           /* title = intent.getStringExtra("title");
            body = intent.getStringExtra("body");
            lastUpdate = intent.getStringExtra("lastUpdate");*/
            Note note = new Note();
            note = getIntent().getParcelableExtra("note");

            editText_title.setText(note.getTitle());
            editText_body.setText(note.getBody());
            textView_lastUpdate.setText(note.getLast_update());

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_body = (EditText) findViewById(R.id.editText_body);
        textView_lastUpdate = (TextView) findViewById(R.id.textView_lastUpdate);

        title = editText_title.getText().toString();
        body = editText_body.getText().toString();
        lastUpdate = "2017";

        //if()
        Note note = new Note();
        note.setTitle(title);
        note.setBody(body);
        note.setLast_update(lastUpdate);
        note.setColour("#ffffff");
        //note.addLabel("Work");
        note.setStatus("active");

        //Creating new user node, which returns the unique key value
        //new user node would be /users/$userid/
        String noteId = myref.push().getKey();

        // pushing user to 'users' node using the userId
        myref.child(noteId).setValue(note);
    }
}
