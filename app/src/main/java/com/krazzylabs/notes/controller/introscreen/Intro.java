package com.krazzylabs.notes.controller.introscreen;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.krazzylabs.notes.R;
import com.krazzylabs.notes.controller.BaseActivity;
import com.krazzylabs.notes.controller.MainActivity;
import com.krazzylabs.notes.model.PrefManager;
import com.krazzylabs.notes.utils.Utils;

public class Intro extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    ViewPager mViewPager;
    IntroAdapter introAdapter;
    PrefManager prefManager;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private String idToken;
    private final Context mContext = this;

    private String name, email,uid;
    private String photo;
    private Uri photoUri;
    SignInButton mSignInButton;

    // Progress Dots
    private int dotsCount=3;    //No of tabs or images
    private ImageView[] dots;
    LinearLayout linearLayout;
    RelativeLayout introMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        configureSignIn();

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (prefManager.getIsLoggedIn()) {
            launchHomeScreen();
            finish();
        }

        // Initializing Default List
        prefManager.setDisplayScreen(getString(R.string.NOTE_ACTIVE));
        introMain = (RelativeLayout) findViewById(R.id.introMain);
        introMain.setBackgroundColor(R.color.bg_screen1);


        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

       // argbEvaluator = new ArgbEvaluator();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        // Set an Adapter on the ViewPager
        final IntroAdapter introAdapter  = new IntroAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(introAdapter);

        mViewPager.setOffscreenPageLimit(3);

        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new IntroPageTransformer());
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        // Hiding Status Bar on Activity
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    //createUserInFirebaseHelper();

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    uid=user.getUid();

                    prefManager.setUid(uid);


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        drawPageSelectionIndicators(0);

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            //addBottomDots(position);
            //Toast.makeText(mContext, "Page"+ position, Toast.LENGTH_SHORT).show();
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == 2) {

                introMain.setBackgroundColor(R.color.bg_screen3);
                mSignInButton = (SignInButton) findViewById(R.id.signin);
                mSignInButton.setSize(SignInButton.SIZE_WIDE);
                mSignInButton.setOnClickListener((View.OnClickListener) mContext);

            }

            drawPageSelectionIndicators(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onClick(View v) {
        Utils utils = new Utils(this);
        int id = v.getId();

        if (id == R.id.signin){
            if (utils.isNetworkAvailable()){
                signIn();
            }else {
                Toast.makeText(Intro.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Intro.this, MainActivity.class));
        finish();
    }

    //Making notification bar transparent
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    // This method configures Google SignIn
    public void configureSignIn(){
        // Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();
                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();


                // Save Data to SharedPreference
                prefManager.setIsLoggedIn(true);
                prefManager.saveEmail(email);
                prefManager.saveName(name);
                prefManager.savePhoto(photo);
                //prefManager.saveToken(idToken);

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                // Google Sign In failed, update UI appropriately
                //Log.e(TAG, "Login Failed. ");
                //Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential){
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(Intro.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }else {
                            // createUserInFirebaseHelper();
                            //Toast.makeText(IntroSlider.this, "Login successful",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intro.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void drawPageSelectionIndicators(int mPosition){
        if(linearLayout!=null) {
            linearLayout.removeAllViews();
        }
        linearLayout=(LinearLayout)findViewById(R.id.layoutDots);
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            if(i==mPosition)
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_selected));
            else
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            linearLayout.addView(dots[i], params);
        }
    }


}
