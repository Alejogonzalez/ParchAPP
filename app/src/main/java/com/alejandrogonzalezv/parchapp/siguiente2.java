package com.alejandrogonzalezv.parchapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class siguiente2 extends ActionBarActivity implements View.OnClickListener {
    TextView txvtest;
    TextView txvtest2;
    private String foto;
    private static int TAKE_PICTURE = 1;
    int aleatorio = 0;
    Bitmap bitmap;
    String encodedString;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siguiente2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        txvtest = (TextView) findViewById(R.id.txv);
        txvtest2 = (TextView) findViewById(R.id.txv2);
        Button btnenviar = (Button) findViewById(R.id.btnenv);
        Button btncamara = (Button) findViewById(R.id.btncam);
        btnenviar.setOnClickListener(this);
        btncamara.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_siguiente2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidM
        // anifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnenv){
            EditText num = (EditText) findViewById(R.id.ednum);
            enviarnum(num.getText().toString());
        }
        if (v.getId()==R.id.btncam){
            aleatorio = new Double(Math.random() * 100).intValue();
            foto = Environment.getExternalStorageDirectory() + "/imagen"+ aleatorio +".png";
            name = "/imagen"+ aleatorio +".png";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri output = Uri.fromFile(new File(foto));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
            startActivityForResult(intent, TAKE_PICTURE); // 1 para la camara, 2 para la galeria
        }
    }
    public static String URL = "http://parchapp.esy.es/prueba.php";
    private void enviarnum(String numero) {
        Map<String, String> params3 = new HashMap<String, String>();
        params3.put("numero", numero);
        params3.put("nombre", "Alekoz");
        final JSONObject Jason=new JSONObject(params3);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,URL,Jason,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resp1 = response.getString("resp1");
                            String resp2 = response.getString("resp2");
                            txvtest.setText(resp1);
                            txvtest2.setText(resp2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {       @Override
                public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en peticion Json", Toast.LENGTH_SHORT).show();
            }
        }
        );

        MySingleton.getInstance(this).addToRequestQueue(getRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.setImageBitmap(BitmapFactory.decodeFile(foto));

        File file = new File(foto);
        if (file.exists()) {
            UploaderFoto nuevaTarea = new UploaderFoto();
            nuevaTarea.execute(foto);
        }
        else
            Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();
    }
    class UploaderFoto extends AsyncTask  <String, Void, Void>{

        ProgressDialog pDialog;
        String miFoto = "";


        @Override
        protected Void doInBackground(String... params) {
            miFoto = params[0];
            try {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(foto);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(siguiente2.this);
            pDialog.setMessage("Subiendo la imagen, espere." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Map<String, String> paramsfoto = new HashMap<String, String>();
            paramsfoto.put("image", encodedString);
            paramsfoto.put("filename",name);
            // Trigger Image upload
            triggerImageUpload(paramsfoto);
            pDialog.dismiss();
        }
    }

    public void triggerImageUpload(Map<String, String> paramsfoto) {
        makeHTTPCall(paramsfoto);
    }
    public static String URLf = "http://parchapp.esy.es/up.php";
    public void makeHTTPCall(Map<String, String> paramsfoto) {
        final JSONObject Jason=new JSONObject(paramsfoto);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,URLf,Jason,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resp1 = response.getString("resp1");
                            txvtest.setText(resp1);
                            String resp2 = response.getString("resp2");
                            txvtest2.setText(resp2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {       @Override
                public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en peticion Json", Toast.LENGTH_SHORT).show();
            }
        }
        );

        MySingleton.getInstance(this).addToRequestQueue(getRequest);

    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }
        private CallbackManager callbackManager;
        private LoginManager loginmanager;
        private TextView textView;
        private TextView txdata;
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
            View rootView = inflater.inflate(R.layout.fragment_siguiente2, container, false);


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

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }



        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);

            /*if (data == null) {
                Intent t = new Intent(getActivity(),MainActivity.class);
                startActivity(t);
            }*/


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
            if (profile==null){
                Intent t = new Intent(getActivity(),MainActivity.class);
                startActivity(t);
            }
            displayMessage(profile);
        }




        private void displayMessage(Profile profile){
            if(profile != null){


            }
        }

    }
}


