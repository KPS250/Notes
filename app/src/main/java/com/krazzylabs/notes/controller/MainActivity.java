package com.krazzylabs.notes.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.controller.list.DividerItemLine;
import com.krazzylabs.notes.controller.list.NotesAdapter;
import com.krazzylabs.notes.controller.list.RecyclerTouchListener;
import com.krazzylabs.notes.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    private NotesAdapter mAdapter;
    private List<Note> noteList = new ArrayList<>();
    private String databaseRef = "notes";
    private static final  String TAG = "DataFB";
    String key;
    private static Boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting Perstistence for Offline use
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }


        // Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myref = database.getReference(databaseRef);



        /* Firebase Integration- Custom Logs
        * You can use Crash.log to log custom events in your crash reports and optionally also the logcat.
        * If you wish to simply log an event and don't want logcat ouput, you only need to pass a string as the argument, as shown in this example:
        */

        //FirebaseCrash.log("Activity created");

        /* Firebase Integration -Exception Reporting
         * You can also generate reports in instances where you catch an exception.
         * You can do this in instances where your application catches an exception but you still want to report the occurrence.
         * The following example demonstrates logging an event to the report and logcat and generating a report after catching an unrecoverable exception.
         */
        //FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
        //FirebaseCrash.report(ex);

        // Floating Button - Create a new Note
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, CreateNote.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Adding Navigation to Activity
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new NotesAdapter(noteList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemLine(this, LinearLayoutManager.VERTICAL));

        // Setting the adapter
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Note note = noteList.get(position);
                Intent intent = new Intent(MainActivity.this, CreateNote.class);
                //intent.putExtra("title", note.getTitle());
                //intent.putExtra("body", note.getBody());
                //intent.putExtra("lastUpdate", note.getLast_update());
                intent.putExtra("note", note);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), note.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        // Read from the database
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                noteList.clear();
                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                Log.d("Data " ,""+dataSnapshot.toString());

                // Looping into different notes
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // Getting Key of Leaf Node
                    key = postSnapshot.getKey();

                    // Getting Leaf Node Parameters
                    Note note1 = postSnapshot.getValue(Note.class);
                    note1.setKey(key);
                    Log.d(TAG, " KEY : "+ key +
                               " Title: " + note1.getTitle() +
                               " Body " + note1.getBody());
                    noteList.add(note1);

                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
