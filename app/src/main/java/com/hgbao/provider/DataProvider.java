package com.hgbao.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.hgbao.io.JsonFactory;
import com.hgbao.model.Act;
import com.hgbao.model.EPhoto;
import com.hgbao.model.Entertainment;
import com.hgbao.model.Scholarship;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * This class is to provide the default strings data, and interact with database or file under internal storage
 */
public final class DataProvider {
    public final static String GOOGLE_BROWSWER_KEY = "AIzaSyCaWbeHld4upvjtDvHOdeE2x9dbHDp2K8k";
    public final static String FACEBOOK_APPLICATION_ID = "1687961114771211";
    //GCM
    public final static String GCM_SenderID = "564472244654";
    public final static String GCM_ACTIVITY = "NEW_ACTIVITY";
    public final static String GCM_ENTERTAINMENT = "NEW_ENTERTAINEMNT";
    public final static String GCM_ENTERTAINMENTPHOTO = "NEW_ENTERTAINMENTPHOTO";
    public final static String GCM_SCHOLARSHIP = "NEW_SCHOLARSHIP";
    public final static String GCM_METHOD_REGISTER = "addDevice";
    public final static String GCM_METHOD_UNREGISTER = "removeDevice";
    public final static String GCM_METHOD_PARAMETER = "regId";

    //Data
    public final static ArrayList<Scholarship> list_scholarship = new ArrayList<>();
    public final static ArrayList<Entertainment> list_entertainment = new ArrayList<>();
    public final static ArrayList<Act> list_activity = new ArrayList<>();

    //Default data and extra data name
    public final static String DATA_URL_FANPAGE = "https://www.facebook.com/www.hcmus.edu.vn?fref=ts";
    public final static String DATA_PHONE_NUMBER = "0838353193";
    public final static String DATA_EMAIL = "ph-daotao@hcmus.edu.vn";
    public final static String DATA_URL_WEBSITE = "http://www.hcmus.edu.vn/index.php";
    public final static String DATA_URL_DOANTN = "http://doantn.hcmus.edu.vn/";
    public final static double UNIVERSITY_LATITUDE = 10.7627166;
    public final static double UNIVERSITY_LONGITUDE = 106.6823101;
    public final static float MAP_ZOOM_ALL = 15;
    public final static float MAP_ZOOM_CURRENT = 16;
    public final static String EXTRA_OPTION_TYPE = "EXTRA_OPTION";
    public final static String EXTRA_ACTIVITY = "OPTION_ACTIVITY";
    public final static String EXTRA_SCHOLARSHIP = "OPTION_SCHOLARSHIP";
    public final static String EXTRA_ENTERTAINMENT = "OPTION_ENTERTAINMENT";
    public final static String EXTRA_OTHER = "OPTION_OTHER";
    public final static String EXTRA_ITEM = "EXTRA_ITEM";
    public final static String EXTRA_LIST = "EXTRA_LIST";

    //Progress
    public final static String PROGRESS_LOAD = "Loading";
    public final static String PROGRESS_CHECK = "Checking for updates";
    public final static String PROGRESS_DOWNLOAD = "Downloading updates";
    public final static String PROGRESS_DOWNLOAD_ACTIVITY = "Downloading new activities";
    public final static String PROGRESS_DOWNLOAD_SCHOLARSHIP = "Downloading new scholarships";
    public final static String PROGRESS_DOWNLOAD_ENTERTAINEMNT = "Downloading new entertainments";
    public final static String PROGRESS_DOWNLOAD_PHOTO = "Downloading new photos";
    //Tasks
    public final static String TASK_DATABASE_ERROR_TYPE = "anyType{}";
    public final static String TASK_DATABASE_NAMESPACE = "http://tempuri.org/";
    public final static String TASK_DATABASE_URL = "http://hgbao.somee.com/hcmushandbookdatabaseservice.asmx";
    public final static String TASK_DATABASE_METHOD_PARAMETER = "date";
    public final static String TASK_DATABASE_METHOD_CHECK = "getNewestVersion";
    public final static String TASK_DATABASE_METHOD_ACTIVITY = "getListActivity";
    public final static String TASK_DATABASE_METHOD_SCHOLARSHIP = "getListScholarship";
    public final static String TASK_DATABASE_METHOD_ENTERTAINMENT = "getListEntertainment";
    public final static String TASK_DATABASE_METHOD_ENTERTAINMENTPHOTO = "getListEntertainmentPhoto";

    //Timing
    public final static int TIME_OUT_REQUEST = 5000;
    public final static int TIME_ANIMATION_FADING = 1500;
    public final static int TIME_VIBRATION = 500;

    //SQLite database
    public final static String DB_PATH_SUFFIX = "/databases/";
    public final static String DATABASE_NAME = "dbHandbook.sqlite";
    public static SQLiteDatabase database = null;
    public static String current_version;
    public static String newest_version;
    /**
     * Initilize database file
     */
    public static void createDatabase(Context context){
        DatabaseHelper helper = new DatabaseHelper(context);
        database = helper.loadDatabase();
    }
    /**
     * Read the last update date of this database
     */
    public static void readUpdate(){
        Cursor cur = database.query("tblUpdate", null, null, null, null, null, null);
        while (cur.moveToNext()) {
            current_version = cur.getString(0);
        }
        cur.close();
    }
    /**
     * Read database from internal storage
     */
    public static void readDatabase() {
        readScholarship();
        int n = list_scholarship.size();
        for (int i = 0; i < n-1; i++){
            for (int j = i+1; j < n; j++){
                if (list_scholarship.get(i).getDateCreated() < list_scholarship.get(j).getDateDeadline()){
                    Scholarship tmp = list_scholarship.get(i);
                    list_scholarship.set(i, list_scholarship.get(j));
                    list_scholarship.set(j, tmp);
                }
            }
        }
        readActivity();
        n = list_activity.size();
        for (int i = 0; i < n-1; i++){
            for (int j = i+1; j < n; j++){
                if (list_activity.get(i).getDateCreated() < list_activity.get(j).getDateDeadline()){
                    Act tmp = list_activity.get(i);
                    list_activity.set(i, list_activity.get(j));
                    list_activity.set(j, tmp);
                }
            }
        }
        readEntertainment();
    }
    private static void readScholarship(){
        list_scholarship.clear();
        Cursor cur = database.query("tblScholarship", null, null, null, null, null, null);
        while (cur.moveToNext()) {
            Scholarship obj = new Scholarship();
            obj.setId(cur.getString(0));
            obj.setType(cur.getInt(1));
            obj.setName(cur.getString(3));
            obj.setAbout(cur.getString(4));
            obj.setSubmitEmail(cur.getString(5));
            obj.setSubmitSubject(cur.getString(6));
            //Date
            obj.setDateCreated(Long.parseLong(cur.getString(2)));
            obj.setDateDeadline(Long.parseLong(cur.getString(7)));
            obj.setDateReceive(Long.parseLong(cur.getString(8)));
            //Expired
            if (obj.getDateDeadline() != 0 && (obj.getDateDeadline() < Calendar.getInstance().getTimeInMillis()))
                obj.setExpired();
            list_scholarship.add(obj);
        }
        cur.close();
    }
    private static void readEntertainment(){
        list_entertainment.clear();
        Cursor cur = database.query("tblEntertainment", null, null, null, null, null, null);
        while (cur.moveToNext()) {
            Entertainment obj = new Entertainment();
            obj.setId(cur.getString(0));
            obj.setType(cur.getInt(1));
            obj.setName(cur.getString(2));
            obj.setAddress(cur.getString(3));
            obj.setDescription(cur.getString(4));
            obj.setAvatar(cur.getBlob(5));
            obj.setRating(cur.getFloat(6));
            obj.setVote(cur.getInt(7));
            obj.setWebsite(cur.getString(8));
            obj.setLatitude(cur.getDouble(9));
            obj.setLongitude(cur.getDouble(10));
            list_entertainment.add(obj);
        }
        cur.close();
        //Read photos
        cur = database.query("tblEntertainmentPhoto", null, null, null, null, null, null);
        String preID = "";
        Entertainment entertainment = new Entertainment();
        while (cur.moveToNext()) {
            String id = cur.getString(0);
            if (id.equalsIgnoreCase(preID))
                entertainment.addList_photo(cur.getBlob(1));
            else {
                preID = id;
                for (int i = 0; i < list_entertainment.size(); i++)
                    if (list_entertainment.get(i).getId().equalsIgnoreCase(preID))
                        entertainment = list_entertainment.get(i);
                entertainment.addList_photo(cur.getBlob(1));
            }
        }
        cur.close();
    }
    private static void readActivity(){
        list_activity.clear();
        Cursor cur = database.query("tblActivity", null, null, null, null, null, null);
        while (cur.moveToNext()) {
            Act obj = new Act();
            obj.setId(cur.getString(0));
            obj.setType(cur.getInt(1));
            obj.setName(cur.getString(3));
            obj.setAbout(cur.getString(4));
            obj.setSubmit(cur.getString(5));
            //Date
            obj.setDateCreated(Long.parseLong(cur.getString(2)));
            obj.setDateDeadline(Long.parseLong(cur.getString(6)));
            obj.setDateOccur(Long.parseLong(cur.getString(7)));
            //Expired
            if (obj.getDateDeadline() != 0 && (obj.getDateDeadline() < Calendar.getInstance().getTimeInMillis()))
                obj.setExpired();
            list_activity.add(obj);
        }
        cur.close();
    }
    //Insert new elementss to database
    public static void insertUpdate(){
        ContentValues row = new ContentValues();
        row.put("Date", newest_version);
        int r = database.update("tblUpdate", row, "Date=?", new String[]{current_version});
    }
    public static void insertActivity(ArrayList<Act> list_obj){
        int n = list_obj.size();
        for (int i = 0; i < n; i++) {
            Act obj = list_obj.get(i);
            ContentValues row = new ContentValues();
            row.put("ID", obj.getId());
            row.put("Type", obj.getType());
            row.put("DateCreated", obj.getDateCreated());
            row.put("Name", obj.getName());
            row.put("About", obj.getAbout());
            row.put("Submit", obj.getSubmit());
            row.put("DateDeadline", obj.getDateDeadline());
            row.put("DateOccur", obj.getDateOccur());
            database.insert("tblActivity", null, row);
        }
    }
    public static void insertScholarship(ArrayList<Scholarship> list_obj){
        int n = list_obj.size();
        for (int i = 0; i < n; i++) {
            Scholarship obj = list_obj.get(i);
            ContentValues row = new ContentValues();
            row.put("ID", obj.getId());
            row.put("Type", obj.getType());
            row.put("DateCreated", obj.getDateCreated());
            row.put("Name", obj.getName());
            row.put("About", obj.getAbout());
            row.put("SubmitEmail", obj.getSubmitEmail());
            row.put("SubmitSubject", obj.getSubmitSubject());
            row.put("SubmitDeadline", obj.getDateDeadline());
            row.put("SubmitReceive", obj.getDateReceive());
            database.insert("tblScholarship", null, row);
        }
    }
    public static void insertEntertainment(ArrayList<Entertainment> list_obj){
        int n = list_obj.size();
        for (int i = 0; i < n; i++) {
            Entertainment obj = list_obj.get(i);
            ContentValues row = new ContentValues();
            row.put("ID", obj.getId());
            row.put("Type", obj.getType());
            row.put("Name", obj.getName());
            row.put("Address", obj.getAddress());
            row.put("Description", obj.getDescription());
            row.put("Avatar", obj.getAvatar());
            row.put("Rating", obj.getRating());
            row.put("Vote", obj.getVote());
            row.put("Website", obj.getWebsite());
            row.put("Latitude", obj.getLatitude());
            row.put("Longitude", obj.getLongitude());
            database.insert("tblEntertainment", null, row);
        }
    }
    public static void insertEntertainmentPhoto(ArrayList<EPhoto> list_obj){
        int n = list_obj.size();
        for (int i = 0; i < n; i++) {
            EPhoto obj = list_obj.get(i);
            ContentValues row = new ContentValues();
            row.put("eID", obj.getEid());
            row.put("Photo", obj.getPhoto());
            database.insert("tblEntertainmentPhoto", null, row);
        }
    }

    //SharedReference
    public final static String SHARED_PREFERENCE = "mySharedPreference";
    public final static String PREFERENCE_NOTIFICATION = "PREF_NOTIFICATION";
    public final static String PREFERENCE_NOTIFICATION_ON = "NOTIFICATION_ON";
    public final static String PREFERENCE_NOTIFICATION_OFF = "NOTIFICATION_OFF";
    public final static String PREFERENCE_USERID = "PREF_FB_USERID";
    public final static String PREFERENCE_TOKEN = "PREF_FB_TOKEN";
    public final static String PREFERENCE_AVATAR = "PREF_FB_AVATAR";
    public final static String PREFERENCE_COVER = "PREF_FB_COVER";
    public final static String PREFERENCE_USERNAME = "PREF_FB_USERNAME";
    public final static String PREFERENCE_LINK = "PREF_FB_LINK";
    /**
     * Set or add new prefernence
     */
    private static void setPreference(Context context, String refName, String refValue){
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(refName, refValue);
        editor.commit();
    }
    /**
     * Get reference data
     */
    private static String getReference(Context context, String refName){
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCE, context.MODE_PRIVATE);
        return pref.getString(refName, "");
    }
    //Notification methods
    public static boolean isReceiveNoti(Context context){
        return getReference(context, PREFERENCE_NOTIFICATION).equalsIgnoreCase(PREFERENCE_NOTIFICATION_ON);
    }
    public static void setReceiveNoti(Context context){
        setPreference(context, PREFERENCE_NOTIFICATION, PREFERENCE_NOTIFICATION_ON);
    }
    public static void unsetReceiveNoti(Context context){
        setPreference(context, PREFERENCE_NOTIFICATION, PREFERENCE_NOTIFICATION_OFF);
    }
    //Facebook methods
    public static boolean isLogin(Context context){
        return !getReference(context, PREFERENCE_USERID).isEmpty();
    }
    public static void login(Context context, String id, String token, String name, String avatar, String cover, String link){
        setPreference(context, PREFERENCE_USERID, id);
        setPreference(context, PREFERENCE_TOKEN, token);
        setPreference(context, PREFERENCE_USERNAME, name);
        setPreference(context, PREFERENCE_AVATAR, avatar);
        setPreference(context, PREFERENCE_COVER, cover);
        setPreference(context, PREFERENCE_LINK, link);
    }
    public static void logout(Context context){
        setPreference(context, PREFERENCE_USERID, "");
        setPreference(context, PREFERENCE_TOKEN, "");
        setPreference(context, PREFERENCE_USERNAME, "");
        setPreference(context, PREFERENCE_AVATAR, "");
        setPreference(context, PREFERENCE_COVER, "");
        setPreference(context, PREFERENCE_LINK, "");
    }
        //Get data from reference
    public static String getUsername(Context context){
        return getReference(context, PREFERENCE_USERNAME);
    }
    public static byte[] getAvatar(Context context){
        byte[] result = Base64.decode(getReference(context, PREFERENCE_AVATAR), Base64.DEFAULT);
        return result;
    }
    public static byte[] getCover(Context context){
        byte[] result = Base64.decode(getReference(context, PREFERENCE_COVER), Base64.DEFAULT);
        return result;
    }
    public static String getLink(Context context){
        return getReference(context, PREFERENCE_LINK);
    }
}