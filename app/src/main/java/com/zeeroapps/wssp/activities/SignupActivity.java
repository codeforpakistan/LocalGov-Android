package com.zeeroapps.wssp.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.admin.AdminDrawerActivity;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.SHA1;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    Button signUp;
    TextView alreadyRegistered, alreadyRegistered2;
    AVLoadingIndicatorView avLoadingIndicatorView;
    EditText etName, etPhone, etEmail, etAddress, etPassword;
    String name, phone, email, address, password;
    Intent intent;
    RelativeLayout relativeLayout;
    private String tag_json_obj = "JSON_OBJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();

        signUp.setOnClickListener(this);
        alreadyRegistered.setOnClickListener(this);
        alreadyRegistered2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int vID = view.getId();

        switch (vID) {
            case R.id.alreadyRegistered:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.alreadyRegistered1:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.signup:
                validation();
                break;
        }
    }

    public void init() {

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        signUp = (Button) findViewById(R.id.signup);
        alreadyRegistered = (TextView) findViewById(R.id.alreadyRegistered);
        alreadyRegistered2 = (TextView) findViewById(R.id.alreadyRegistered1);
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        avLoadingIndicatorView.hide();

    }

    public void validation() {

        name = etName.getText().toString();
        phone = etPhone.getText().toString();
        address = etAddress.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (name.equals("")) {
            etName.setError("Enter Name");
            etName.requestFocus();
        } else if (phone.equals("")) {
            etPhone.setError("Enter Phone Number");
            etPhone.requestFocus();
        } else if (email.equals("")) {
            etEmail.setError("Enter Email");
            etEmail.requestFocus();
        } else if (password.equals("")) {
            etPassword.setError("Enter Password");
            etPassword.requestFocus();
        } else if (address.equals("")) {
            etAddress.setError("Enter Address");
            etAddress.requestFocus();
        } else if (phone.length() < 11){
            etPhone.setError("Invalid Phone Number");
            etPhone.requestFocus();
        } else if (!email.matches(emailPattern)) {
            etEmail.requestFocus();
            etEmail.setError("Invalid Email Address");
            Snackbar.make(relativeLayout, "invalid email address", Snackbar.LENGTH_LONG).show();
        } else {

            RegisterUser();

        }
    }


    public void RegisterUser() {


        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.SignUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("ServerResponse", response.toString());
                avLoadingIndicatorView.hide();

                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    String status = jObj.getString("success");

                    if (status.contains("User Registered Successfully")) {


                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        Snackbar.make(relativeLayout, "Email / Phone Number Already Exists", Snackbar.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                avLoadingIndicatorView.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorResponse", error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(relativeLayout, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(relativeLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }
                avLoadingIndicatorView.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("phone", phone);
                params.put("email", email);
                params.put("address", address);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);


    }




}
