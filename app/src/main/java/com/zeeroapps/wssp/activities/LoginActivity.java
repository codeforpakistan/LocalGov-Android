package com.zeeroapps.wssp.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.SQLite.DatabaseHelper;
import com.zeeroapps.wssp.admin.AdminDrawerActivity;
import com.zeeroapps.wssp.receivers.ConnectivityStateReceiver;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.DistrictsListGetSet;
import com.zeeroapps.wssp.utils.SHA1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

public class LoginActivity extends Activity implements View.OnClickListener {


    RelativeLayout mainLayout;
    EditText etPhone, etPass;
    Button btnLogin, btnHelp;
    AVLoadingIndicatorView avi;
    SharedPreferences sp;
    SharedPreferences.Editor spEdit;

    private static final String TAG = "MyApp";
    private String tag_json_obj = "JSON_OBJECT";
    private String token;

    String passwordSHA1;
    private String phoneNo, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = this.getSharedPreferences(getString(R.string.sp), this.MODE_PRIVATE);
        spEdit = sp.edit();

        initUIControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String scrName = "LOGIN SCREEN";
    }

    private void initUIControls() {
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPass = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnHelp = (Button) findViewById(R.id.btnHelp);
        avi = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        avi.hide();

        btnLogin.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
    }

    public void validateFields() {
        phoneNo = etPhone.getText().toString();
        password = etPass.getText().toString();

        if (TextUtils.isEmpty(phoneNo)) {
            etPhone.requestFocus();
            etPhone.setError("Enter valid phone number!");
            return;
        } else if (password.length() < 5) {
            etPass.requestFocus();
            etPass.setError("Password must be 5 characters long!");
            return;
        }
        avi.show();
        loginWS();
    }

    @Override
    public void onClick(View view) {
        int vID = view.getId();

        switch (vID) {
            case R.id.btnLogin:
//                Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
//                startActivity(intent);
//                finish();
                validateFields();
                break;
            case R.id.btnHelp:
                Intent intent = new Intent(LoginActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void forgotPassword(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@wssp.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Forgot my account password!");
        i.putExtra(Intent.EXTRA_TEXT, "Kindly create a new password for me.");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LoginActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginWS() {
        try {
            passwordSHA1 = SHA1.encrypt(etPass.getText().toString());
            Log.e(TAG, "loginWS: " + passwordSHA1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());

                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    String status = jObj.getString("status");

                    if (status.contains("Success")) {
                        Snackbar.make(mainLayout, status, Snackbar.LENGTH_LONG).show();

                        spEdit.putString(getString(R.string.spUID), jObj.getString("account_id"));
                        spEdit.putString(getString(R.string.spUMobile), jObj.getString("mobilenumber"));
                        spEdit.putString("userType", jObj.getString("user_type"));

                        if (jObj.getString("user_type").equals("user")) {
                            spEdit.putString(getString(R.string.spUName), jObj.getString("username"));
                            spEdit.commit();
                            Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            spEdit.commit();
                            Intent intent = new Intent(LoginActivity.this, AdminDrawerActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    } else {
                        Snackbar.make(mainLayout, "Incorrect Username or Password", Snackbar.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(mainLayout, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mainLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }
                avi.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber", phoneNo);
                params.put("password", password);
                params.put("token_id", sp.getString("FB_TOKEN", null));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);


    }


    public void getMemberDetailsWS() {
        avi.show();


        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.URL_MEMBERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());

                try {
                    JSONArray jArr = new JSONArray(response);
                    JSONObject jObj = jArr.getJSONObject(0);

                    if (jObj.toString().toLowerCase().contains("account_id")) {
                        Snackbar.make(mainLayout, "Success!", Snackbar.LENGTH_LONG).show();
                        spEdit.putString(getString(R.string.spUID), jObj.getString("account_id"));
                        spEdit.putString(getString(R.string.spUMobile), jObj.getString("mobilenumber"));
                        spEdit.putString(getString(R.string.spUName), jObj.getString("fullname"));
                        spEdit.putString(getString(R.string.spUEmail), jObj.getString("emailad"));
                        spEdit.putString(getString(R.string.spUPic), jObj.getString("profile_image"));
                        spEdit.putString(getString(R.string.spUC), jObj.getString("uc_id"));
                        spEdit.putString(getString(R.string.spNC), jObj.getString("nc_id"));
                        spEdit.putString("userType", jObj.getString("user_type"));
                        spEdit.commit();


                        if (jObj.getString("user_type").equals("user")) {
                            Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, AdminDrawerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(mainLayout, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mainLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }
                avi.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber", etPhone.getText().toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);
    }
}