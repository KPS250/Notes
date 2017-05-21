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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.controller.list.DividerItemLine;
import com.krazzylabs.notes.controller.list.NotesAdapter;
import com.krazzylabs.notes.controller.list.RecyclerTouchListener;
import com.krazzylabs.notes.model.FirebaseHelper;
import com.krazzylabs.notes.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recyclerView;
    private static NotesAdapter mAdapter;
    private static List<Note> noteList = new ArrayList<>();
    private static final  String TAG = "DataFB";

    // Navigation HEader Elements
    TextView textView_userName, textView_userEmail;
    ImageView imageView_user;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting FirebaseHelper Instance
        firebaseHelper = new FirebaseHelper();

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
                finish();
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

        // Set Navigation Header Elements
        setNavigationHeader(navigationView);

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
                intent.putExtra("note", note);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        // Read from the database
        firebaseHelper.getMyref().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Clearing Existing List Data
                noteList.clear();

                // Looping into different notes
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // Getting Key of Leaf Node
                    String key = postSnapshot.getKey();

                    // Getting Leaf Node Parameters
                    Note note = postSnapshot.getValue(Note.class);
                    note.setKey(key);
                    noteList.add(note);
                }
                //noteList = firebaseHelper.getNoteList(dataSnapshot);
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

    // Set Navigation Header Elements
    public void setNavigationHeader(NavigationView navigationView){

        // Changing Navigation Header Elements
        View hView =  navigationView.getHeaderView(0);

        imageView_user = (ImageView)hView.findViewById(R.id.imageView_user);
        textView_userName = (TextView)hView.findViewById(R.id.textViw_username);
        textView_userEmail = (TextView)hView.findViewById(R.id.textView_email);

        imageView_user.setImageResource(R.drawable.ic_menu_camera);
        textView_userName.setText("Kiran Shinde");
        textView_userEmail.setText("kiran_shinde@gmail.com");
    }
}
