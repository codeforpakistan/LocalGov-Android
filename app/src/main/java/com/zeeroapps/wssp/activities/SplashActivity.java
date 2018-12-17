package com.zeeroapps.wssp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.SQLite.DatabaseHelper;
import com.zeeroapps.wssp.admin.AdminDrawerActivity;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.DistrictsListGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends Activity {

    DatabaseHelper databaseHelper = null;
    SharedPreferences sp;
    int rows_count;
    ImageView ivSplash;
    FirebaseAnalytics analytics;
    ArrayList<DistrictsListGetSet> districtsArraylist = new ArrayList<DistrictsListGetSet>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        databaseHelper = new DatabaseHelper(this);
        getDistrictsData();


        sp = getSharedPreferences(getString(R.string.sp), MODE_PRIVATE);

        analytics = FirebaseAnalytics.getInstance(this);

        ivSplash = (ImageView) findViewById(R.id.ivSplash);
        scaleAnimation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sp.getString(getString(R.string.spUMobile), null) == null) {
                    startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                } else if (sp.getString("userType", null).equals("user")){
                    startActivity(new Intent(SplashActivity.this, DrawerActivity.class));
                }
                else if (sp.getString("userType", null).equals("admin")){
                    startActivity(new Intent(SplashActivity.this, AdminDrawerActivity.class));
                }
                finish();
            }
        }, 4000);
    }


    public void scaleAnimation() {
        final Animation scaleAnim = new ScaleAnimation(
                1f, 5f,
                1f, 5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(1500);
//        scaleAnim.setRepeatCount(2);
//        scaleAnim.setRepeatMode(Animation.REVERSE);
        scaleAnim.setInterpolator(this, android.R.anim.bounce_interpolator);
        scaleAnim.setFillAfter(true);
        scaleAnim.setFillBefore(true);
        ivSplash.setAnimation(scaleAnim);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.e("Animm...", "Endd....");

            }
        });
    }



    //server call
    public void getDistrictsData() {

        String urlGetServerData = Constants.END_POINT+"Districts";
        System.out.print(urlGetServerData);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGetServerData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response + "");
                        try {
                            Gson gson = new Gson();
                            JSONArray jsonArray = response.getJSONArray("Data");

                            //get object from json
                            String responseText = response.getString("success");
                            Log.e("response", responseText + "");

                            for (int p = 0; p < jsonArray.length(); p++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(p);
                                DistrictsListGetSet listGetSet = gson.fromJson(String.valueOf(jsonObject), DistrictsListGetSet.class);
                                districtsArraylist.add(listGetSet);

                            }

                            //get value from sharedpreferences
                            SharedPreferences.Editor editor = sp.edit();
                            String db_version = sp.getString("db_version", "0");

                            rows_count = databaseHelper.getCount();

                            if (db_version.equals(districtsArraylist.get(0).getDb_version())) {

                                Log.e("db_version", "database version matched"+"\n"+ rows_count
                                +"\tversion=\t\t" + districtsArraylist.get(0).getDb_version());

                            } else {

                                Log.e("db_version", "database version not matched");
                                //check if database is created and version is different then
                                //delete previous database
                                if (rows_count > 0) {
                                    databaseHelper.deleteDataFromDistrictTable();
                                }

                                //Adding values to sharedpreferences
                                editor.putString("db_version", districtsArraylist.get(0).getDb_version());
                                editor.apply();

                                //add data to sqlite database
                                if (districtsArraylist.size() > 0) {

                                    for (int i = 0; i < districtsArraylist.size(); i++) {

                                        databaseHelper.addDistrictsToDB(districtsArraylist.get(i).getId(),
                                                districtsArraylist.get(i).getDistricts_categories(),
                                                districtsArraylist.get(i).getLevel(),
                                                districtsArraylist.get(i).getParent_id(),
                                                districtsArraylist.get(i).getDb_version(),
                                                districtsArraylist.get(i).getSlug());

                                    }
                                }

                            }

                            Log.e("listSize", districtsArraylist.size() + "\n" + districtsArraylist.get(0).getDb_version());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println(error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String name = "SPLASH SCREEN";

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IMAGE");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
