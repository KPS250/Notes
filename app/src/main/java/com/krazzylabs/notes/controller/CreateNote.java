package com.krazzylabs.notes.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    Handler h = new Handler();
    int delay = 10000; //15 seconds
    Runnable runnable;
    private static MenuItem menuItem;

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
    protected void onStart() {
     //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                menuItem.setIcon(R.drawable.ic_menu_camera);
                saveNote();
                Toast.makeText(CreateNote.this, "AutoSaved", Toast.LENGTH_SHORT).show();
                menuItem.setIcon(R.drawable.cloudsync);
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onStart();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        h.removeCallbacks(runnable); //stop handler when activity not visible
        saveNote();

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
        menuItem = menu.findItem(R.id.action_save);
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
            h.removeCallbacks(runnable); //stop handler when activity not visible
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, note.getTitle());
            startActivity(Intent.createChooser(shareIntent, "Share Note"));

            return true;
        }

        if (id == R.id.action_delete) {

            // Deleting Note
            h.removeCallbacks(runnable); //stop handler when activity not visible
            Intent intent = new Intent(CreateNote.this, MainActivity.class);
            intent.putExtra("action", "trash");
            intent.putExtra("note",new Note(this.note));
            firebaseHelper.trashNote(this.note);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_archive) {

            // Archiving Note
            h.removeCallbacks(runnable); //stop handler when activity not visible
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

    public void saveNote(){
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

            if (!this.note.keyExists())
             {
                 //Create a new Note
                String key = firebaseHelper.createNote(new Note(this.note));
                this.note.setKey(key);
             } else {
                //Update the existing Note
                firebaseHelper.updateNote((new Note(this.note)));
             }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
