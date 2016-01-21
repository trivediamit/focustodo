package com.simplelilfe.focustodo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.simplelilfe.focustodo.R;
import com.simplelilfe.focustodo.fragment.CreateTaskFragment;
import com.simplelilfe.focustodo.fragment.SettingsFragment;
import com.simplelilfe.focustodo.fragment.TasksFragment;
import com.simplelilfe.focustodo.helper.GoogleSignInHelper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleSignInHelper.GoogleSignInCallback, GoogleSignInHelper.GoogleSignOutCallback, TasksFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener, CreateTaskFragment.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = "Home";

    public static final int RC_SIGN_IN = 0;

    private TextView textViewUsername, textViewEmail;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String firebaseURL = "https://focus-to-do.firebaseio.com/";

    private GoogleSignInHelper googleSignInHelper;
    private String googleAccountID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

        googleSignInHelper = new GoogleSignInHelper(this, this, this);
        // Check if Google Play Services are available on the device.
        if (!googleSignInHelper.checkPlayServices()) {
            Toast.makeText(Home.this, "Google Play Services are not available on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set default values for all the Settings/Preferences of the app
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Initialize Google API Client
        googleSignInHelper.initGoogleAPIClient();

        // Show the first fragment as Tasks Fragment
        replaceContentFragment(0);
    }

    @Override
    public void onStart() {
        super.onStart();

        googleSignInHelper.silentSignIn();
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

        if (id == R.id.nav_tasks) {
            replaceContentFragment(0); // Tasks
        } else if (id == R.id.nav_settings) {
            replaceContentFragment(1); // Settings
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
        // TODO Handle further
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        } else {
            Log.e(TAG, "onActivityResult: Error getting ActivityResult");
            // TODO Handle further
        }
    }

    private void setupViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textViewUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_bar_username);
        textViewUsername.setOnClickListener(this);

        textViewEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_bar_email);
    }

    @Override
    public void handleResult(GoogleSignInResult result) {
        Log.d(TAG, "handleResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            googleAccountID = acct.getId();

            // Update UI
            updateUI(true, acct.getDisplayName(), acct.getEmail());
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false, "", "");
        }
    }

    @Override
    public void handleResult(Status status) {
        // TODO Check status
        updateUI(false, "", "");
    }

    private void updateUI(boolean signedIn, String username, String emailID) {
        if (signedIn) {
            textViewUsername.setText(username);
            textViewUsername.setPaintFlags(textViewUsername.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
            textViewUsername.setOnClickListener(null); // Remove On Click Listener

            textViewEmail.setText(emailID);

            // Update Shared Preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putString(SettingsFragment.PREF_SIGNED_IN, "Signed in as: " + emailID).apply();
        } else {
            textViewUsername.setText(getString(R.string.sign_in));
            textViewUsername.setPaintFlags(textViewUsername.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textViewUsername.setOnClickListener(this);

            textViewEmail.setText(getString(R.string.info_not_signed_in));

            // Update Shared Preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putString(SettingsFragment.PREF_SIGNED_IN, "Not signed in").apply();
        }
    }

    @Override
    public void onFragmentInteraction(String action) {
        if ("sign_out".equalsIgnoreCase(action)) {
            googleSignInHelper.signOut();
        } else if ("add".equalsIgnoreCase(action)) {

            CreateTaskFragment createTaskFragment = CreateTaskFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, createTaskFragment).commit();
        }
    }

    private void replaceContentFragment(int fragmentID) {
        switch (fragmentID) {
            case 0:
                // Tasks
                TasksFragment tasksFragment = TasksFragment.newInstance(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, tasksFragment).commit();
                navigationView.setCheckedItem(R.id.nav_tasks);
                break;

            case 1:
                // Settings
                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, settingsFragment).commit();
                navigationView.setCheckedItem(R.id.nav_settings);
                break;

            default:
                // TODO: 06-01-2016 handle later
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_bar_username:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    googleSignInHelper.signIn();
                }
                break;
            default:
                // TODO Handle this later
        }
    }
}
