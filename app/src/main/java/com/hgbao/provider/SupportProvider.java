package com.hgbao.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.hgbao.hcmushandbook.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * This class is to provide the methods for converting, get data from url or checking state, set state of controls
 * For general, this class is to interact with UI or URL
 */
public final class SupportProvider {
    /**
     * Set an event to calendar
     */
    public static void setCalendarEvent(Activity context, String title, String location, String description,
                                        boolean allDay, long timeStart, long timeEnd){
        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra(CalendarContract.Events.TITLE, title);
        i.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        i.putExtra(CalendarContract.Events.DESCRIPTION, description);
        i.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
        i.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeStart);
        i.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeEnd);
        context.startActivity(i);
    }

    /**
     * Convert a date to string type
     */
    public static String stringDate(long dateInMillis){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(dateInMillis);
        String result;
        int day =  date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;
        int year = date.get(Calendar.YEAR);
        if (day < 10)
            result = "0" + day + "/";
        else
            result = day + "/";
        if (month < 10)
            result = result + "0" + month + "/";
        else
            result = result + month + "/";
        return result + year;
    }

    /**
     * Resize a drawable due to width and height in dimens resource
     */
    public static Drawable resizeDrawable(Activity context, int imageID, int dimenWidth, int dimenHeight) {
        Drawable image = ContextCompat.getDrawable(context, imageID);
        Bitmap bitmap = ((BitmapDrawable)image).getBitmap();
        //Resize
        int width = Math.round(context.getResources().getDimension(dimenWidth));
        int height = Math.round(context.getResources().getDimension(dimenHeight));
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return new BitmapDrawable(context.getResources(), bitmapResized);
    }

    /**
     * Get byte array from link
     */
    public static byte[] getByteArrayFromURL(Activity context, String url){
        Bitmap bitmap = getBitmapFromURL(url);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        else{
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.error_no_image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
    }
    public static Bitmap getBitmapFromURL(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DataProvider.TIME_OUT_REQUEST);
            connection.setReadTimeout(DataProvider.TIME_OUT_REQUEST);
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check network state
     */
    public static boolean isNetworkConnected(Activity context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }

    /**
     * Set status of a checkbox menu item
     */
    public static boolean checkItem(MenuItem item){
        if (item.isChecked()){
            item.setChecked(false);
            item.setIcon(R.drawable.ic_checkbox_uncheck);
            return false;
        }
        else{
            item.setChecked(true);
            item.setIcon(R.drawable.ic_checkbox_check);
            return true;
        }
    }
}
