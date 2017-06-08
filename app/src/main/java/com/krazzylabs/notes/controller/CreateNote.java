package com.krazzylabs.notes.controller;

import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.model.FirebaseHelper;
import com.krazzylabs.notes.model.Note;

public class CreateNote extends AppCompatActivity {

    EditText editText_title, editText_body;
    TextView textView_lastUpdate;
    String title, body, lastUpdate;

    public static Note note;
    public static FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // Creating Note Instance
        this.note = new Note();

        // Creating FirebaseHelper Instance
        this.firebaseHelper = new FirebaseHelper(this);

        // Linking UI Elements
        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_body = (EditText) findViewById(R.id.editText_body);
        textView_lastUpdate = (TextView) findViewById(R.id.textView_lastUpdate);

        // Catching Existing Note
        Intent intent = getIntent();
        if(intent.hasExtra("note")){

            this.note = getIntent().getParcelableExtra("note");

            editText_title.setText(this.note.getTitle());
            editText_body.setText(this.note.getBody());
            textView_lastUpdate.setText(this.note.getLast_update());
        }

    }

    @Override
    public void onBackPressed() {

        // Catching Edited Note Attributes
        title = editText_title.getText().toString();
        body = editText_body.getText().toString();
        lastUpdate = "2017";

        try {
            // Setting Note Attributes
            this.note.setTitle(title);
            this.note.setBody(body);
            this.note.setLast_update(lastUpdate);
            this.note.setColour("#ffffff");
            //note.addLabel("Work");
            this.note.setStatus("active");

            // Checking Note Existence
            Boolean flag = note.keyExists();

            if (flag) {
                //Create a new Note
                firebaseHelper.createNote(this.note);

            } else {
                //Update the existing Note
                firebaseHelper.updateNote(this.note);
            }
        }catch(Exception e){
                e.printStackTrace();
            }

        Intent intent = new Intent(CreateNote.this, MainActivity.class);
        startActivity(intent);
        finish();

        // Commented for Custom Handling of Activity Stack
        // super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    // Option Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, note.getTitle());
            startActivity(Intent.createChooser(shareIntent, "Share Note"));

            return true;
        }

        if (id == R.id.action_delete) {

            // Deleting Note
            Intent intent = new Intent(CreateNote.this, MainActivity.class);
            intent.putExtra("action", "trash");
            intent.putExtra("note",new Note(this.note));
            firebaseHelper.trashNote(this.note);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_archive) {

            // Archiving Note
            Intent intent = new Intent(CreateNote.this, MainActivity.class);
            intent.putExtra("action", "archive");
            intent.putExtra("note",new Note(this.note));
            firebaseHelper.archiveNote(this.note);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
