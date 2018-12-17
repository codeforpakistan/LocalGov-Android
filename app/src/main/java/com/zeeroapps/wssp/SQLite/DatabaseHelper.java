package com.zeeroapps.wssp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fazalullah on 4/6/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wssp_database.db";
    private static final String TABLE_NAME = "wssp_complaint";
    private static final String TABLE_DISTRICTS = "wssp_districts";
    private static final String KEY_ID = "id_";
    private static final String USER_ID = "user_id";
    private static final String COMPLAINT_NUMBER = "complaint_id";
    private static final String COMPLAINT_TYPE = "c_type";
    private static final String COMPLAINT_TIME = "c_time";
    private static final String COMPLAINT_DETAIL = "c_detail";
    private static final String COMPLAINT_IMAGE = "c_image";
    private static final String COMPLAINT_LAT = "c_lat";
    private static final String COMPLAINT_LNG = "c_lng";
    private static final String COMPLAINT_ADDRESS = "bin_address";
    private static final String COMPLAINT_STATUS = "c_status";
    public static final  String DISTRICT_ID = "id";
    public static final  String DISTRICT_NAME = "districts_categories";
    public static final  String LEVEL = "level";
    public static final  String PARENT_ID = "parent_id";
    public static final  String SERVER_DB_VERSION = "db_version";
    public static final  String KEY_SERIAL = "s_no";
    public static final  String SLUG = "slug";



    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                USER_ID + " TEXT, " +
                COMPLAINT_NUMBER + " TEXT, " +
                COMPLAINT_TYPE + " TEXT, " +
                COMPLAINT_TIME + " TEXT, " +
                COMPLAINT_DETAIL + " TEXT, " +
                COMPLAINT_IMAGE + " TEXT, " +
                COMPLAINT_LAT + " TEXT, " +
                COMPLAINT_LNG + " TEXT, " +
                COMPLAINT_ADDRESS + " TEXT, " +
                COMPLAINT_STATUS + " TEXT " +
                ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String sql = "CREATE TABLE " + TABLE_DISTRICTS
                + "(" + KEY_SERIAL +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + DISTRICT_ID +
                " VARCHAR, " + DISTRICT_NAME +
                " VARCHAR, " + LEVEL + " VARCHAR, "
                + SERVER_DB_VERSION + " VARCHAR, "
                + SLUG + " VARCHAR, "
                + PARENT_ID + " VARCHAR);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addComplaintToDB(String userId, String complaintID, String complaintType, String currentDateandTime, String image, String lat, String lng, String address, String cDetails, String status) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, userId);
        values.put(COMPLAINT_NUMBER, complaintID);
        values.put(COMPLAINT_TYPE, complaintType);
        values.put(COMPLAINT_TIME, currentDateandTime);
        values.put(COMPLAINT_DETAIL, cDetails);
        values.put(COMPLAINT_IMAGE, image);
        values.put(COMPLAINT_LAT, lat);
        values.put(COMPLAINT_LNG, lng);
        values.put(COMPLAINT_ADDRESS, address);
        values.put(COMPLAINT_STATUS, status);

        long res = db.insert(TABLE_NAME, null, values);
        db.close();
        if (res == -1) return false;
        else return true;
    }

    public Cursor getAllComplaints(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return cursor;
    }

    public void deleteDataFromTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public boolean addDistrictsToDB(String district_id, String district_name,
                                    String level, String parent_id, String db_version, String slug) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DISTRICT_ID, district_id);
        values.put(DISTRICT_NAME, district_name);
        values.put(LEVEL, level);
        values.put(PARENT_ID, parent_id);
        values.put(SERVER_DB_VERSION, db_version);
        values.put(SLUG, slug);


        db.insert(TABLE_DISTRICTS, null, values);
        db.close();
        return true;
    }

    public Cursor getDistrictsNames() {

            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM " + TABLE_DISTRICTS + " WHERE  "+LEVEL+" = 0;";
            Cursor c = db.rawQuery(sql, null);
            return c;

    }


    public Cursor getTehsilNames(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DISTRICTS + " WHERE  "+PARENT_ID+" = '"+id+"' ;";
        Cursor c = db.rawQuery(sql, null);
        return c;

    }


    public Cursor getDistrictID(String name) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DISTRICTS + " WHERE  "+DISTRICT_NAME+" = '"+name+"' ;";
        Cursor c = db.rawQuery(sql, null);
        return c;

    }


    public Cursor getTehsilSlug(String name) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DISTRICTS + " WHERE  "+DISTRICT_NAME+" = '"+name+"' ;";
        Cursor c = db.rawQuery(sql, null);
        return c;

    }


    public void deleteDataFromDistrictTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DISTRICTS, null, null);
        db.close();
    }


    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DISTRICTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
