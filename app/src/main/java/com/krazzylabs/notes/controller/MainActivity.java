package com.krazzylabs.notes.controller;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.controller.introscreen.Intro;
import com.krazzylabs.notes.controller.list.NotesAdapter;
import com.krazzylabs.notes.controller.list.RecyclerTouchListener;
import com.krazzylabs.notes.model.FirebaseHelper;
import com.krazzylabs.notes.model.Note;
import com.krazzylabs.notes.model.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity  extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recyclerView;
    private static NotesAdapter mAdapter;

    private static final  String TAG = "DataFB";

    // Navigation Header Elements
    private TextView textView_userName, textView_userEmail, textView;
    private CircleImageView imageView_user;
    private FirebaseHelper firebaseHelper;
    private ProgressBar progressBar;
    private Paint p = new Paint();
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private PrefManager prefManager;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    NavigationView navigationView;


    Context mContext = this;
    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    private String mUsername, mEmail,uid,userName,userEmail,userPhoto;
    Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // create an object of sharedPreferenceManager and get stored user data
        prefManager = new PrefManager(mContext);
        mUsername = prefManager.getName();
        mEmail = prefManager.getUserEmail();
        String uri = prefManager.getPhoto();
        mPhotoUri  = Uri.parse(uri);

        configureSignIn();

        //Hide ToolBar
        //getSupportActionBar().show();
        getSupportActionBar().setShowHideAnimationEnabled(true);


        // Setting FirebaseHelper Instance
        firebaseHelper = new FirebaseHelper(this);
        prefManager = new PrefManager(this);

       // prefManager.setDisplayScreen(getString(R.string.NOTE_ACTIVE));
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNote.class);
                startActivity(intent);
                finish();
            }
        });

        // Deleting Note from CreateNote Activity
        Intent intent = getIntent();
        if(intent.hasExtra("action")){

            firebaseHelper.setLastSelectedNote(new Note((Note)getIntent().getParcelableExtra("note")));

            if(intent.getStringExtra("action").equals("trash")){
                Snackbar.make(fab,
                        "Note Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new UndoTrashSnackListener()).show();
            }else if(intent.getStringExtra("action").equals("archive")){
                Snackbar.make(fab,
                        "Note Archive", Snackbar.LENGTH_LONG).setAction("UNDO", new UndoTrashSnackListener()).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Adding Navigation to Activity
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Navigation Header Elements
        setNavigationHeader(navigationView);



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //textView = (TextView) findViewById(R.id.textView);
        //textView.setVisibility(View.VISIBLE);

        mAdapter = new NotesAdapter(firebaseHelper.getDefaultNoteList());

        setToolbarTitle();

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

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

                /*
                if (direction == ItemTouchHelper.LEFT) {
                    // Delete Note
                    firebaseHelper.trashNote(firebaseHelper.getNoteList().get(position));
                    Snackbar.make(fab, "Note Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new UndoTrashSnackListener()).show();
                } else {
                    // Edit
                    Intent intent = new Intent(MainActivity.this, CreateNote.class);
                    intent.putExtra("note", firebaseHelper.getNoteList().get(position));
                    startActivity(intent);
                    finish();
                }
                */
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                /*
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
                */
                    }

                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (actionMode != null) {
                    toggleSelection(position);
                }else{
                    Intent intent = new Intent(MainActivity.this, CreateNote.class);
                    intent.putExtra("note", firebaseHelper.getDefaultNoteList().get(position));
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public int onLongClick(View view, int position) {
                if (actionMode == null) {
                    actionMode = startSupportActionMode(actionModeCallback);
                }

                toggleSelection(position);
                return position;
            }
        }));


        // Read from the database
        firebaseHelper.getMyref().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                progressBar.setVisibility(View.VISIBLE);

                firebaseHelper.getNoteList(dataSnapshot);
                mAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Hidimg FAB on List Scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fab.isShown()) {
                    fab.hide();
                }

                if(dy<0 & actionMode != null)
                {
                    //textView.setVisibility(View.VISIBLE);

                }//else
                //textView.setVisibility(View.GONE);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fab.show();

                }
                super.onScrollStateChanged(recyclerView, newState);
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

                /*progressBar.setVisibility(View.VISIBLE);
                noteList.clear();
                noteList = searchNoteList(query);
                AdapterDataRefresh();
                progressBar.setVisibility(View.GONE);
                */
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(query)){
                    mAdapter = new NotesAdapter(firebaseHelper.getDisplayScreenNote());

                }else{
                    mAdapter = new NotesAdapter(firebaseHelper.searchNoteList(query));
                }

                AdapterDataRefresh();
                progressBar.setVisibility(View.GONE);
                return true;
            }

        });

        return true;
    }

    // Menu Action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switchView) {
            prefManager.setDefaultViewSwitch();
            AdapterDataRefresh();
            if(prefManager.getDefaultViewSwitch())
                item.setIcon(R.drawable.ic_action_stream);
            else
                item.setIcon(R.drawable.ic_action_stag);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // SideNavigationDrawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            prefManager.setDisplayScreen(getString(R.string.NOTE_ACTIVE));
            mAdapter = new NotesAdapter(firebaseHelper.getDefaultNoteList());
            setToolbarTitle();
            AdapterDataRefresh();
        } else if (id == R.id.nav_archive) {
            prefManager.setDisplayScreen(getString(R.string.NOTE_ARCHIVE));
            mAdapter = new NotesAdapter(firebaseHelper.getDefaultNoteList());
            setToolbarTitle();
            AdapterDataRefresh();
        }else if (id == R.id.nav_bin) {
            prefManager.setDisplayScreen(getString(R.string.NOTE_TRASH));
            mAdapter = new NotesAdapter(firebaseHelper.getDefaultNoteList());
            setToolbarTitle();
            AdapterDataRefresh();
        } /*else if (id == R.id.nav_red) {

        } else if (id == R.id.nav_green) {

        }*/

        else if (id == R.id.nav_settings) {
            signOut();
                     }



        else if (id == R.id.nav_archive) {


        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Set Navigation Header Elements
    public void setNavigationHeader(NavigationView navigationView){

        // Changing Navigation Header Elements
        View hView =  navigationView.getHeaderView(0);

        imageView_user = (CircleImageView)hView.findViewById(R.id.imageView_user);
        textView_userName = (TextView)hView.findViewById(R.id.textViw_username);
        textView_userEmail = (TextView)hView.findViewById(R.id.textView_email);

        Picasso.with(mContext)
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(imageView_user);
        textView_userName.setText(mUsername);
        textView_userEmail.setText(mEmail);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        imageView_user.setImageResource(R.drawable.ic_menu_options_logout);
        textView_userName.setText("");
        textView_userEmail.setText("");

    }


    public void AdapterDataRefresh(){

        //mAdapter = new NotesAdapter(firebaseHelper.getNoteList());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemLine(getApplicationContext(), LinearLayoutManager.VERTICAL));

        if(!prefManager.getDefaultViewSwitch()){
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
        }

        // Setting the adapter
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {

        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {

            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);

            MenuItem actionDelete = menu.findItem(R.id.action_delete);
            MenuItem actionTrash = menu.findItem(R.id.action_trash);
            MenuItem actionArchive = menu.findItem(R.id.action_archive);
            MenuItem actionRestore = menu.findItem(R.id.action_restore);

            // show the button when some condition is true
            if (prefManager.getDisplayScreen().equals(getString(R.string.NOTE_ACTIVE))) {
                actionRestore.setVisible(false);
                actionDelete.setVisible(false);
            }else if (prefManager.getDisplayScreen().equals(getString(R.string.NOTE_TRASH))) {
                actionTrash.setVisible(false);
            }else if (prefManager.getDisplayScreen().equals(getString(R.string.NOTE_ARCHIVE))) {
                actionArchive.setVisible(false);
                actionDelete.setVisible(false);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_delete:
                    Log.d(TAG, "menu_remove");
                    firebaseHelper.selectedDelete(new ArrayList<Integer>(mAdapter.getSelectedItems()));
                    Snackbar.make(fab, "Notes Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new selectedUndoTrashSnackListener()).show();
                    mode.finish();
                    return true;

                case R.id.action_trash:
                    Log.d(TAG, "menu_remove");
                    firebaseHelper.selectedTrash(new ArrayList<Integer>(mAdapter.getSelectedItems()));
                    Snackbar.make(fab, "Notes Trashed", Snackbar.LENGTH_LONG).setAction("UNDO", new selectedUndoTrashSnackListener()).show();
                    mode.finish();
                    return true;

                case R.id.action_archive:
                    Log.d(TAG, "menu_archive");
                    firebaseHelper.selectedArchive(new ArrayList<Integer>(mAdapter.getSelectedItems()));
                    Snackbar.make(fab, "Notes Archived", Snackbar.LENGTH_LONG).setAction("UNDO", new selectedUndoTrashSnackListener()).show();
                    mode.finish();
                    return true;

                case R.id.action_restore:
                    Log.d(TAG, "menu_remove");
                    firebaseHelper.selectedRestore(new ArrayList<Integer>(mAdapter.getSelectedItems()));
                    Snackbar.make(fab, "Notes Restored", Snackbar.LENGTH_LONG).setAction("UNDO", new selectedUndoTrashSnackListener()).show();
                    mode.finish();
                    return true;


                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }
    }

    public class UndoTrashSnackListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            firebaseHelper.activateNote();
            AdapterDataRefresh();
        }
    }

    public class selectedUndoTrashSnackListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            firebaseHelper.selectedUndoTrash();
            AdapterDataRefresh();
        }
    }

    public void setToolbarTitle(){

        Log.d("DefaultScreen1", prefManager.getDisplayScreen() );
        final Menu menu = navigationView.getMenu();

        if(prefManager.getDisplayScreen().equals(getString(R.string.NOTE_ACTIVE))) {
            toolbar.setTitle(getString(R.string.NOTE_ACTIVE));
            menu.getItem(0).setChecked(true);
        }else if(prefManager.getDisplayScreen().equals(getString(R.string.NOTE_ARCHIVE))) {
            toolbar.setTitle(getString(R.string.NOTE_ARCHIVE));
            menu.getItem(1).setChecked(true);
        }else if(prefManager.getDisplayScreen().equals(getString(R.string.NOTE_TRASH))) {
            toolbar.setTitle(getString(R.string.NOTE_TRASH));
            menu.getItem(2).setChecked(true);
        }

        Log.d("DefaultScreen2", (String) toolbar.getTitle());

    }

    // This method configures Google SignIn
    public void configureSignIn(){
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    //method to logout
    private void signOut(){
        prefManager.setIsLoggedIn(false);
        prefManager.clear();
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent intent = new Intent(MainActivity.this, Intro.class);
                        startActivity(intent);
                    }
                });
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return  true;
        } else {
            //Toast.makeText(this, "Internet Connection Is Required", Toast.LENGTH_LONG).show();
            return  false;
        }
    }
}

