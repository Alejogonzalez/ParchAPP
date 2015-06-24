package com.alejandrogonzalezv.parchapp;


import android.content.Intent;
import android.service.textservice.SpellCheckerService;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;



public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }
        private CallbackManager callbackManager;
        private TextView textView;

        private AccessTokenTracker accessTokenTracker;
        private ProfileTracker profileTracker;
        private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                displayMessage(profile);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {   }
        };
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            callbackManager = CallbackManager.Factory.create();

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                }
            };
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                    displayMessage(newProfile);
                }
            };

            accessTokenTracker.startTracking();
            profileTracker.startTracking();

            return rootView;
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);
            LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
            textView = (TextView) view.findViewById(R.id.dataStatus);

            loginButton.setReadPermissions("user_friends");
            loginButton.setFragment(this);
            loginButton.registerCallback(callbackManager, callback);
        }



        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                Intent t = new Intent(getActivity(),siguiente.class);
                startActivity(t);
            }


        }
        @Override
        public void onStop() {
            super.onStop();
            accessTokenTracker.stopTracking();
            profileTracker.stopTracking();
        }
        @Override
        public void onResume() {
            super.onResume();
            Profile profile = Profile.getCurrentProfile();
            if (profile !=null){
                Intent t = new Intent(getActivity(),siguiente.class);
                startActivity(t);
            }
            displayMessage(profile);
        }




        private void displayMessage(Profile profile){
            if(profile != null){
                textView.setText(profile.getName());

            }
        }

    }
}
