package com.krazzylabs.notes.controller;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.model.FirebaseHelper;
import com.krazzylabs.notes.model.Note;

public class CreateNote extends AppCompatActivity implements FragmentMenuDialog.ISelectedData {

    public ImageButton imageButton_white,imageButton_yellow, imageButton_chrome,imageButton_green,imageButton_pink,imageButton_blue,imageButton_purple,imageButton_grey;
    EditText editText_title, editText_body;
    TextView textView_lastUpdate;
    String title, body, lastUpdate;
    ImageButton imageButton_menu;
    LinearLayout ll_optionsMenu;
    LinearLayout ll_share,ll_archive,ll_trash;

    public static Note note;
    public static FirebaseHelper firebaseHelper;

    Handler h = new Handler();
    int delay = 10000; //15 seconds
    Runnable runnable;
    private static MenuItem menuItem;
    FragmentMenuDialog dialogFragment;

    private BottomSheetBehavior mBottomSheetBehavior;

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
        //textView_lastUpdate = (TextView) findViewById(R.id.textView_lastUpdate);

        GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setStroke(10,getResources().getColor(R.color.grey));

        ll_optionsMenu = (LinearLayout) findViewById(R.id.ll_optionsMenu);
        ll_optionsMenu .setBackground(gradientDrawable);

        imageButton_menu = (ImageButton) findViewById(R.id.imageButton_menu);
        imageButton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

               /* FragmentManager fm = getFragmentManager();
                dialogFragment = new FragmentMenuDialog ();
                dialogFragment.setTargetFragment(dialogFragment, 1);

                Window window = dialogFragment.getDialog().getWindow();//.requestFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams param = window.getAttributes();
                param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                window.setAttributes(param);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                dialogFragment.show(fm, "Sample Fragment");
                */

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Hide: Soft keyboard
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                imageButton_white= (ImageButton) findViewById(R.id.imageButton_white);
                imageButton_white.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                       note.setColour(String.valueOf(getString(R.color.white)));
                       colorBackground();
                    }
                });

                imageButton_yellow = (ImageButton) findViewById(R.id.imageButton_yellow);
                imageButton_yellow.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.yellow)));
                        colorBackground();
                    }
                });

                imageButton_chrome = (ImageButton) findViewById(R.id.imageButton_chrome);
                imageButton_chrome.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.chrome)));
                        colorBackground();
                    }
                });

                imageButton_green = (ImageButton) findViewById(R.id.imageButton_green);
                imageButton_green.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.green)));
                        colorBackground();
                    }
                });

                imageButton_pink = (ImageButton) findViewById(R.id.imageButton_pink);
                imageButton_pink.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.pink)));
                        colorBackground();
                    }
                });

                imageButton_blue = (ImageButton) findViewById(R.id.imageButton_blue);
                imageButton_blue.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.blue)));
                        colorBackground();
                    }
                });

                imageButton_purple = (ImageButton) findViewById(R.id.imageButton_purple);
                imageButton_purple.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.purple)));
                        colorBackground();
                    }
                });

                imageButton_grey = (ImageButton) findViewById(R.id.imageButton_grey);
                imageButton_grey.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        note.setColour(String.valueOf(getString(R.color.grey)));
                        colorBackground();
                    }
                });

                ll_share = (LinearLayout) findViewById(R.id.ll_share);
                ll_share.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, CreateNote.note.getTitle());
                        startActivity(Intent.createChooser(shareIntent, "Share Note"));
                    }
                });

                ll_archive = (LinearLayout) findViewById(R.id.ll_archive);
                ll_archive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        firebaseHelper.archiveNote(note);
                        Intent intent = new Intent(CreateNote.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });

                ll_trash = (LinearLayout) findViewById(R.id.ll_trash);
                ll_trash.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        firebaseHelper.trashNote(note);
                        Intent intent = new Intent(CreateNote.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });

            }
        });


        // Catching Existing Note
        Intent intent = getIntent();
        if(intent.hasExtra("note")){

            this.note = getIntent().getParcelableExtra("note");

            editText_title.setText(this.note.getTitle());
            editText_body.setText(this.note.getBody());
            //textView_lastUpdate.setText(this.note.getLast_update());
            colorBackground();
        }
        //mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        View bottomSheet = findViewById(R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    @Override
    protected void onStart() {
     //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                //menuItem.setIcon(R.drawable.ic_menu_camera);
                //saveNote();
                //Toast.makeText(CreateNote.this, "AutoSaved", Toast.LENGTH_SHORT).show();
                //menuItem.setIcon(R.drawable.ic_action_cloud);
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
    protected void onDestroy() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_note, menu);
        //menuItem = menu.findItem(R.id.action_save);
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
            //this.note.setColour("#ffffff");
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

    @Override
    public void onSelectedData(String userSelection) {
        //if(userSelection.equals(String.valueOf(getString(R.color.yellow)))
            this.note.setColour(userSelection);


        colorBackground();
    }

    public void colorBackground(){
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor(this.note.getColour()));
        ll_optionsMenu.setBackgroundColor(Color.parseColor(this.note.getColour()));
    }
}


