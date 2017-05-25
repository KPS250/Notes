package com.krazzylabs.notes.controller;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.krazzylabs.notes.model.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recyclerView;
    private static NotesAdapter mAdapter;
    private static List<Note> noteList = new ArrayList<>();
    private static List<Note> noteListBackup = new ArrayList<>();
    private static final  String TAG = "DataFB";

    // Navigation HEader Elements
    TextView textView_userName, textView_userEmail;
    ImageView imageView_user;
    FirebaseHelper firebaseHelper;
    ProgressBar progressBar;
    private Paint p = new Paint();
    SearchView searchView;
    MenuItem searchMenuItem;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting FirebaseHelper Instance
        firebaseHelper = new FirebaseHelper();
        prefManager = new PrefManager(this);

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //progressBar.setVisibility(View.VISIBLE);
        mAdapter = new NotesAdapter(noteList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        //recyclerView.addItemDecoration(new DividerItemLine(this, LinearLayoutManager.VERTICAL));

        // Setting the adapter
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    // Delete Note
                    firebaseHelper.deleteNote(noteList.get(position));
                    Snackbar.make(viewHolder.itemView, "Note Deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();


                } else {
                    // Edit
                    Note note = noteList.get(position);
                    Intent intent = new Intent(MainActivity.this, CreateNote.class);
                    intent.putExtra("note", note);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#A5D6A7"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.security);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        //c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#EF9A9A"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.security);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        //c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
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

                progressBar.setVisibility(View.VISIBLE);

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
                progressBar.setVisibility(View.GONE);
                noteListBackup = noteList;
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

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBar.setVisibility(View.VISIBLE);
                noteList = searchNoteList(query);
                AdapterDataRefresh();
                progressBar.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(query)){
                    //Toast.makeText(MainActivity.this,"Empty",Toast.LENGTH_SHORT).show();
                    noteList = noteListBackup;

                }else{
                    noteList = searchNoteList(query);
                }

                AdapterDataRefresh();
                progressBar.setVisibility(View.GONE);
                return true;
            }

        });

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
        }else if (id == R.id.action_switchView) {
            Log.d("Switch","Yes");
            prefManager.setDefaultViewSwitch();
            AdapterDataRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            // Handle the camera action
        } else if (id == R.id.nav_bin) {

        } else if (id == R.id.nav_red) {

        } else if (id == R.id.nav_green) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

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

    public List<Note> searchNoteList(String query){
        List<Note>  newlist = new ArrayList<>();

        //Traversal throgh Notes in List
        for(Note note : this.noteListBackup) {
            if(note.getTitle() != null && note.getTitle().contains(query)
                    || note.getBody()!= null && note.getBody().contains(query)) {
                newlist.add(note);
            }
        }

      return newlist;
    }

    public void AdapterDataRefresh(){

        mAdapter = new NotesAdapter(noteList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemLine(getApplicationContext(), LinearLayoutManager.VERTICAL));

        if(!prefManager.getDefaultViewSwitch()){
            gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
            recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        }

        // Setting the adapter
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
