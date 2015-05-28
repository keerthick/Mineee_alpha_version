package com.mineee.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.mineee.controller.FeedListAppController;
import com.mineee.modal.LoggedUserSessionData;
import com.mineee.modal.SessionPreferencesManager;
import com.mineee.util.JsonPArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;


public class MineeeSplashActivity extends Activity  implements
        View.OnClickListener,
        ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btn_gplus_SignIn;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private CallbackManager callbackManager;
    private LoginButton btn_fb_SignIn;


    private String URL_FEED;
    LoggedUserSessionData session ;
    AccessToken accessToken;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SessionPreferencesManager.contains(getApplicationContext(),"LOGGED_USER_ID")){
            startNextIntent();
            return;
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_mineee_splash);

        btn_gplus_SignIn = (SignInButton) findViewById(R.id.gLoginButton);
        btn_gplus_SignIn.setOnClickListener(this);

        mGoogleApiClient = new Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                        // Optionally, add additional APIs and scopes if required.
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        callbackManager = CallbackManager.Factory.create();
        btn_fb_SignIn = (LoginButton) findViewById(R.id.fbLoginButton);

        btn_fb_SignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken acc = loginResult.getAccessToken();


                Log.d(TAG, acc.getToken());
                Log.d(TAG, acc.getExpires().toString());
                Log.d(TAG, acc.getApplicationId());

                btn_fb_SignIn.setVisibility(View.GONE);

                accessToken = loginResult.getAccessToken();
                Log.v("VERBOSE",accessToken.getToken());
                //Intent i = new Intent(MineeeSplashActivity.this, FeedMainActivity.class);
                URL_FEED = "http://mineee.com/api/index.php?rquest=AuthenticateInsert&accessToken="+ accessToken.getToken()+"&oauthProvider=1&device=mineee";


                showProgressBar();
                fetchUserId(URL_FEED);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                            // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        //mGoogleApiClient.connect();
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult == null) {

            mIntentInProgress = false;
            mGoogleApiClient.connect();

        } else if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        String accessToken = "";
        try {
            /*accessToken = GoogleAuthUtil.getToken(
                    getApplicationContext(),
                    Plus.AccountApi.getAccountName(mGoogleApiClient), "oauth2:"
                            + Scopes.PLUS_LOGIN );

            System.out.println("Access token==" + accessToken);*/
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String token = null;

                    try {
                        token = GoogleAuthUtil.getToken(
                                MineeeSplashActivity.this,
                                Plus.AccountApi.getAccountName(mGoogleApiClient),
                                "oauth2:" + Scopes.PLUS_LOGIN);

                        URL_FEED = "http://mineee.com/api/index.php?rquest=AuthenticateInsert&accessToken="+ token+"&oauthProvider=2&device=mineee";

                    } catch (IOException transientEx) {
                        // Network or server error, try later
                        Log.e(TAG, transientEx.toString());
                    } catch (UserRecoverableAuthException e) {
                        // Recover (with e.getIntent())
                        Log.e(TAG, e.toString());
                        //Intent recover = e.getIntent();
                        //startActivityForResult(recover, REQUEST_CODE_TOKEN_AUTH);
                    } catch (GoogleAuthException authEx) {
                        // The call is not ever expected to succeed
                        // assuming you have already verified that
                        // Google Play services is installed.
                        Log.e(TAG, authEx.toString());
                    }

                    return token;
                }

                @Override
                protected void onPostExecute(String token) {
                    Log.i(TAG, "Access token retrieved:" + token);
                    showProgressBar();
                    fetchUserId(URL_FEED);
                }

            };
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "User is connected!" + accessToken, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            super.onActivityResult(requestCode, responseCode, intent);
            callbackManager.onActivityResult(requestCode, responseCode, intent);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gLoginButton:
                // Signin button clicked
                signInWithGplus();
                break;
        }
    }

    private void fetchUserId(final String URL_FEED){
        JsonPArrayRequest jsonReq = new JsonPArrayRequest(URL_FEED, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                VolleyLog.d(TAG, "Response: in fetch  " + response.toString());
                if (response != null) {
                    //hidePDialog();
                    parseJsonFeed(response);
                    hidePDialog();
                    //updateUI();
                    startNextIntent();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,URL_FEED);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                //hidePDialog();
            }
        });

        FeedListAppController.getInstance().addToRequestQueue(jsonReq);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONArray response) {
        try {


            JSONObject feedObj = response.getJSONObject(0);

            session = new LoggedUserSessionData();

            session.setLoggedUserId(feedObj.getString("id"));
            session.setName(feedObj.getString("name"));

            SessionPreferencesManager.setLoggedUserID(getApplicationContext(),feedObj.getString("id"));

            Log.d(TAG, feedObj.getString("id"));
            Log.d(TAG,feedObj.getString("name"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void showProgressBar(){
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
    }

    private void startNextIntent(){
         Intent i = new Intent(MineeeSplashActivity.this, TabbedFeedActivity.class);
                if(accessToken != null)
                    i.putExtra("accesstoken",accessToken.getToken());
                if(session != null)
                    i.putExtra("userId", session.getLoggedUserId());
                startActivity(i);
                this.finish();
    }
}