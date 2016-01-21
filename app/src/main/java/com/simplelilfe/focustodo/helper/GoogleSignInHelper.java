package com.simplelilfe.focustodo.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.simplelilfe.focustodo.activity.Home;
import com.simplelilfe.focustodo.util.Utilities;

/**
 * @author amit
 *         Helper Class to do the Google Sign In related operations
 */
public class GoogleSignInHelper implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleSignInHelper";

    private GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;

    private Context context;
    private GoogleSignInCallback googleSignInCallback;
    private GoogleSignOutCallback googleSignOutCallback;

    private Utilities utilities = new Utilities();

    public GoogleSignInHelper(Context context, GoogleSignInCallback googleSignInCallback, GoogleSignOutCallback googleSignOutCallback) {
        this.context = context;
        this.googleSignInCallback = googleSignInCallback;
        this.googleSignOutCallback = googleSignOutCallback;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Home) context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                // TODO Signal to exit
            }
            return false;
        }
        return true;
    }

    public void initGoogleAPIClient() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((Home) context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void silentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            googleSignInCallback.handleResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            utilities.showProgressDialog(context);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    utilities.hideProgressDialog();
                    googleSignInCallback.handleResult(googleSignInResult);
                }
            });
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((Home) context).startActivityForResult(signInIntent, Home.RC_SIGN_IN);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        googleSignOutCallback.handleResult(status);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        googleSignInCallback.onConnectionFailed(connectionResult);
    }

    public interface GoogleSignInCallback {
        void handleResult(GoogleSignInResult googleSignInResult);

        void onConnectionFailed(ConnectionResult connectionResult);
    }

    public interface GoogleSignOutCallback {
        void handleResult(Status status);
    }
}
