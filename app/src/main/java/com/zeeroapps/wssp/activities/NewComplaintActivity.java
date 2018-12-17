package com.zeeroapps.wssp.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.SQLite.DatabaseHelper;
import com.zeeroapps.wssp.receivers.ConnectivityStateReceiver;
import com.zeeroapps.wssp.services.MyLocation;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.CheckNetwork;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.DistrictsListGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class NewComplaintActivity extends Activity {

    private static final int REQUEST_CAMERA_CODE = 1;
    ImageView ivPreview;
    EditText etAddress, etDetails;
    Button btnSubmit, btnRetake;
    String districtSlug, tehsilSlug;
    TextView tvType, tvTypeUrdu, tvZone, tvDistrict, tvTehsil;
    AVLoadingIndicatorView avi;

    String TAG = "MyApp";
    private String mCurrentPhotoPath;
    private String encodedString;

    RelativeLayout llMain;
    private String tag_json_obj = "JSON_TAG";

    Double lat, lng;
    String gpsLocName;
    String complaintID;
    String complaintType;
    String currentDateandTime, district_name, tehsil_name;
    String complaintTypeList[] = {"Drainage", "Trash Bin", "Water Supply", "Garbage", "Other"};
    String complaintTypeListUrdu[] = {"نکاسی آب", "بھرا ہوا گند کا ڈھبہ", "پانی کا مسئلہ", "کوڑا کرکٹ", "کوئی اور مسئلہ"};

    SharedPreferences sp;
    FirebaseAnalytics mFBAnalytics;

    SpinnerDialog districtSpinner, tehsilSpinner;
    ArrayList<String> distristList, tehsilList;
    List<DistrictsListGetSet> DistrictNames, TehsilNames, DistrictId, TehsilSlug;
    DatabaseHelper databaseHelper;
    ArrayList<DistrictsListGetSet> districtsArraylist = new ArrayList<DistrictsListGetSet>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        databaseHelper = new DatabaseHelper(this);
        distristList   = new ArrayList<String>();
        tehsilList     = new ArrayList<String>();
        DistrictNames  = new ArrayList<>();
        TehsilNames    = new ArrayList<>();
        DistrictId     = new ArrayList<>();
        TehsilSlug     = new ArrayList<>();

        //check if database has data
        //get number of total rows
        int rows_count = databaseHelper.getCount();

        if (rows_count > 0) {

            loadDistrictsNames();
        } else {

            getDistrictsData();
        }


        mFBAnalytics = FirebaseAnalytics.getInstance(this);

        sp = this.getSharedPreferences(getString(R.string.sp), MODE_PRIVATE);
        initUIComponents();

        Boolean camFlag = sp.getBoolean("OPEN_CAMERA", false);
//        if (camFlag) {
        sp.edit().putBoolean("OPEN_CAMERA", false).apply();
        openCamera();
//        }
        getTypeIDandTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String scrName = "NEW COMPLAINT SCREEN";
    }

    void initUIComponents() {
        llMain = (RelativeLayout) findViewById(R.id.activity_new_complaint);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDetails = (EditText) findViewById(R.id.etDetails);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        tvType = (TextView) findViewById(R.id.tvTypeEng);
        tvTypeUrdu = (TextView) findViewById(R.id.tvTypeUrdu);
        tvZone = (TextView) findViewById(R.id.tvZone);
        tvDistrict = (TextView) findViewById(R.id.tvUC);
        tvTehsil = (TextView) findViewById(R.id.tvNC);
        avi = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        avi.hide();


        //extract districts names from arraylist and add to String arraylist
        for (int i = 0; i < DistrictNames.size(); i++) {

            distristList.add(DistrictNames.get(i).getDistricts_categories());
        }

        districtSpinner = new SpinnerDialog(this, distristList, "Select District", R.style.DialogAnimations_SmileWindow);// With 	Animation

        districtSpinner.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                getDistrictID(item);
                tvDistrict.setText(item);
            }
        });
        findViewById(R.id.tvUC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                districtSpinner.showSpinerDialog();
            }
        });


        tehsilSpinner = new SpinnerDialog(this, tehsilList, "Select Tehsil", R.style.DialogAnimations_SmileWindow);// With 	Animation

        tehsilSpinner.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                tvTehsil.setText(item);
                getTehsilSlug(item);
            }
        });
        findViewById(R.id.tvNC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tehsilSpinner.showSpinerDialog();
            }
        });


    }

    void getTypeIDandTime() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            int i = intent.getExtras().getInt("selected_item");
            String complaint_Type = intent.getExtras().getString("complaintType");

            if (i == 404){
                tvType.setText(complaint_Type);
                complaintType = tvType.getText().toString();
            }else {
                complaintType = complaintTypeList[i];
                tvType.setText(complaintType);
                tvTypeUrdu.setText(complaintTypeListUrdu[i]);
            }

        }


        Long time = System.currentTimeMillis() / 1000;
        complaintID = Long.toString(time, 30).toUpperCase();
        Log.e(TAG, "Complaint Number: " + complaintID);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        currentDateandTime = dateFormat.format(date);
        Log.e(TAG, "onCreate: " + currentDateandTime);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    photoURI = Uri.fromFile(photoFile);
                } else {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.zeeroapps.wssp.fileprovider",
                            photoFile);
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
            }
        }
    }

    // Create image name
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void handlePicAndCoordinates() {
        ViewTreeObserver vto = ivPreview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // Get the dimensions of the View
                int targetW = ivPreview.getMeasuredWidth();
                int targetH = ivPreview.getMeasuredHeight();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                ivPreview.setImageBitmap(bitmap);
            }
        });

        MyLocation myLocation = new MyLocation(this);
        lat = myLocation.getLatitude();
        lng = myLocation.getLongitude();
        gpsLocName = myLocation.getLocationName();
        Log.e(TAG, "Latitude: " + lat + " Longitude: " + lng + " Location Name: " + gpsLocName);
        myLocation.stopUsingGPS();
        stopService(new Intent(NewComplaintActivity.this, MyLocation.class));
    }

    /**
     * Method To convert image to base64 format
     *
     * @return
     */
    private String encodeImage() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        byte[] array = stream.toByteArray();
        encodedString = Base64.encodeToString(array, 0);
        return Base64.encodeToString(array, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
            handlePicAndCoordinates();
        } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_CANCELED) {
            Intent mainIntent = new Intent(NewComplaintActivity.this, DrawerActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }
    }

    public void validateFields() {


        if (TextUtils.isEmpty(etAddress.getText())) {
            etAddress.requestFocus();
            etAddress.setError("Enter valid Address!");
            return;
        }

        if (TextUtils.isEmpty(etDetails.getText())) {
            etDetails.requestFocus();
            etDetails.setError("Enter valid Description!");
            return;
        }


        btnSubmit.setEnabled(false);
        if (CheckNetwork.isOnline(this)) {
            Log.e(TAG, "Interent Available - Data submitted");
            sendDataToDB();
        } else {
            Log.e(TAG, "No Interent Available - Data saved to SQLite");
            storeDataInSQLite();
            registerBroadcast();
        }

        Bundle bundle = new Bundle();
        bundle.putString("complaint_type", complaintType);
        bundle.putString("complaint_area", etAddress.getText().toString());
        mFBAnalytics.logEvent("complaint", bundle);
    }

    public void submitData(View v) {
        district_name = tvDistrict.getText().toString().trim();
        tehsil_name   = tvTehsil.getText().toString().trim();


        if (district_name.equals("Select District")){
            tvDistrict.requestFocus();
            tvDistrict.setError("Select District");
        }
        else if (tehsil_name.equals("Select Tehsil")){
            etAddress.requestFocus();
            tvTehsil.setError("Select Tehsil");
        }
        else
        validateFields();
    }

    public void sendDataToDB() {
        avi.show();
        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.URL_NEW_COMP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response.toString());
                        avi.hide();
                        Snackbar.make(llMain, response, Snackbar.LENGTH_LONG).show();
                        if (response.toLowerCase().contains("success")) {
                            Intent intent = new Intent(NewComplaintActivity.this, ThankYouActivity.class);
                            intent.putExtra("COMPLAINT_NUMBER", complaintID);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                avi.hide();
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(llMain, "Internet Not Available!", Snackbar.LENGTH_LONG).show();
                } else {
                }
                btnSubmit.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("account_id", sp.getString(getString(R.string.spUID), null));
                params.put("c_number", complaintID);
                params.put("c_type", complaintType);
                params.put("c_date_time", currentDateandTime);
                params.put("c_details", etDetails.getText().toString());
                params.put("image_path", encodeImage());
                params.put("latitude", lat.toString());
                params.put("longitude", lng.toString());
                params.put("bin_address", etAddress.getText().toString());
                params.put("status", "pendingreview");
                params.put("district_slug", districtSlug);
                params.put("district_tma_slug", tehsilSlug);

                return params;
            }
        };
        jsonReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);
    }

    public void storeDataInSQLite() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Boolean dataStored = dbHelper.addComplaintToDB(
                sp.getString(getString(R.string.spUID), null),
                complaintID,
                complaintType,
                currentDateandTime,
                encodeImage(),
                lat.toString(),
                lng.toString(),
                etAddress.getText().toString(),
                etDetails.getText().toString(),
                "pendingreview"
        );
        if (dataStored) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Internet Not available!")
                    .setMessage("Complaint temporarily stored in mobile database. Connect your phone to internet as soon as possible.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(NewComplaintActivity.this, DrawerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
        }
    }

    private void registerBroadcast() {
        PackageManager pm = getPackageManager();
        ComponentName cn = new ComponentName(NewComplaintActivity.this, ConnectivityStateReceiver.class);
        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Log.e(TAG, "Broadcast: ENABLED!");
    }


    SharedPreferences sharedPreferences;

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
                            Log.e("success:", responseText + "");

                            for (int p = 0; p < jsonArray.length(); p++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(p);
                                DistrictsListGetSet listGetSet = gson.fromJson(String.valueOf(jsonObject), DistrictsListGetSet.class);
                                districtsArraylist.add(listGetSet);

                            }

                            //get value from sharedpreferences
                            sharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String db_version = sharedPreferences.getString("db_version", "0");

                            if (db_version.equals(districtsArraylist.get(0).getDb_version())) {

                                //get number of total rows
                                int profile_counts = databaseHelper.getCount();
                                Log.e("db_version", "database version matched" + "\n" + profile_counts);

                            } else {

                                Log.e("db_version", "database version not matched");
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

                            loadDistrictsNames();
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


    //load districts names into spinner
    private void loadDistrictsNames() {

        DistrictNames.clear();

        Cursor cursor = databaseHelper.getDistrictsNames();
        if (cursor.moveToFirst()) {
            do {
                DistrictsListGetSet names = new DistrictsListGetSet(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.LEVEL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PARENT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVER_DB_VERSION)),
                        "dummy", "dummy",
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLUG)));
                DistrictNames.add(names);
            } while (cursor.moveToNext());

//            Toast.makeText(this, DistrictNames.size()+"", Toast.LENGTH_SHORT).show();

        }
    }


    //load tehsils names into spinner
    private void loadTehsilNames(String districtId) {

        TehsilNames.clear();

        Cursor cursor = databaseHelper.getTehsilNames(districtId);
        if (cursor.moveToFirst()) {
            do {
                DistrictsListGetSet names = new DistrictsListGetSet(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.LEVEL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PARENT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVER_DB_VERSION)),
                        "dummy", "dummy",
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLUG)));
                TehsilNames.add(names);
            } while (cursor.moveToNext());

            //extract tehsil names from arraylist and add to String arraylist
            for (int i = 0; i < TehsilNames.size(); i++) {

                tehsilList.add(TehsilNames.get(i).getDistricts_categories());
            }

        }
    }


    //load tehsils names into spinner
    private void getDistrictID(String DistrictName) {

        DistrictId.clear();

        Cursor cursor = databaseHelper.getDistrictID(DistrictName);
        if (cursor.moveToFirst()) {
            do {
                DistrictsListGetSet names = new DistrictsListGetSet(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.LEVEL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PARENT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVER_DB_VERSION)),
                        "dummy", "dummy",
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLUG)));
                DistrictId.add(names);
            } while (cursor.moveToNext());

            districtSlug = DistrictId.get(0).getSlug();
            loadTehsilNames(DistrictId.get(0).getId());

        }
    }


    //load tehsils names into spinner
    private void getTehsilSlug(String TehsilName) {

        TehsilSlug.clear();

        Cursor cursor = databaseHelper.getTehsilSlug(TehsilName);
        if (cursor.moveToFirst()) {
            do {
                DistrictsListGetSet names = new DistrictsListGetSet(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTRICT_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.LEVEL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PARENT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVER_DB_VERSION)),
                        "dummy", "dummy",
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLUG)));

                TehsilSlug.add(names);
            } while (cursor.moveToNext());

            tehsilSlug = TehsilSlug.get(0).getSlug();
        }
    }
}
