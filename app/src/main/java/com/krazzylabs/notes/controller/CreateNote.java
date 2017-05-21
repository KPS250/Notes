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
import com.krazzylabs.notes.model.Note;

public class CreateNote extends AppCompatActivity {

    EditText editText_title, editText_body;
    TextView textView_lastUpdate;
    String title, body, lastUpdate;

    public static FirebaseDatabase database;
    public static DatabaseReference myref;

    public static Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_body = (EditText) findViewById(R.id.editText_body);
        textView_lastUpdate = (TextView) findViewById(R.id.textView_lastUpdate);
        Intent intent = getIntent();
        this.note = new Note();
        if(intent.hasExtra("note")){

           /* title = intent.getStringExtra("title");
            body = intent.getStringExtra("body");
            lastUpdate = intent.getStringExtra("lastUpdate");*/
            this.note = getIntent().getParcelableExtra("note");

            editText_title.setText(this.note.getTitle());
            editText_body.setText(this.note.getBody());
            textView_lastUpdate.setText(this.note.getLast_update());

        }else{


        }

    }

    @Override
    public void onBackPressed() {

        // Write a message to the database
        this.database = FirebaseDatabase.getInstance();

        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_body = (EditText) findViewById(R.id.editText_body);
        textView_lastUpdate = (TextView) findViewById(R.id.textView_lastUpdate);

        title = editText_title.getText().toString();
        body = editText_body.getText().toString();
        lastUpdate = "2017";

        try {
            this.note.setTitle(title);
            this.note.setBody(body);
            this.note.setLast_update(lastUpdate);
            this.note.setColour("#ffffff");
            //note.addLabel("Work");
            this.note.setStatus("active");
            Boolean flag = false;
            try {
                flag = this.note.getKey()==null;
            }catch (Exception e){
              flag = false;
            }
            this.myref = database.getReference("notes");

            if (flag) {

                // If we need to create a new Note
                //Creating new user node, which returns the unique key value
                //new user node would be /users/$userid/
                if(note.getTitle().equals("") && note.getBody().equals("")){

                }else{
                    String noteId = myref.push().getKey();

                    // pushing user to 'users' node using the userId
                    myref.child(noteId).setValue(this.note);
                }


            } else {

                // If we just need to update the existing Note
                if( note.getTitle().equals("") && note.getBody().equals("")) {

                }else{
                    String key = this.note.getKey();
                    this.note.removeKey();
                    //this.myref = database.getReference("notes");

                    // pushing user to 'users' node using the userId
                    myref.child(key).setValue(this.note);
                }
            }
        }catch(Exception e){
                e.printStackTrace();
            }

        //super.onBackPressed();
        Intent intent = new Intent(CreateNote.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

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

            deleteNote(this.note);
            Intent intent = new Intent(CreateNote.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteNote(Note note){
        Boolean flag = false;
        try {
            flag = this.note.getKey()==null;
        }catch (Exception e){
            flag = false;
        }

        if(!flag){
            // Write a message to the database
            this.database = FirebaseDatabase.getInstance();
            this.myref = this.database.getReference("notes");
            String key = this.note.getKey();
            //this.myref = database.getReference("notes");

            // pushing user to 'users' node using the userId
            myref.child(key).setValue(null);

        }else{

        }
    }
}
