package com.hgbao.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DataProvider.DATABASE_NAME, null, 1);
        this.context = context;
    }

    /**
     * Load the database from internal storage
     */
    public SQLiteDatabase loadDatabase(){
        File dbFile = context.getDatabasePath(DataProvider.DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
            } catch (IOException e){

            }
        }
        return context.openOrCreateDatabase(DataProvider.DATABASE_NAME, context.MODE_PRIVATE, null);
    }
    /**
     * Copy database from assets folder for the first time installing
     */
    private void CopyDataBaseFromAsset() throws IOException{
        try {
            InputStream myInput = context.getAssets().open(DataProvider.DATABASE_NAME);
            String outFileName = context.getApplicationInfo().dataDir + DataProvider.DB_PATH_SUFFIX + DataProvider.DATABASE_NAME;

            File f = new File(context.getApplicationInfo().dataDir + DataProvider.DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();

            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}