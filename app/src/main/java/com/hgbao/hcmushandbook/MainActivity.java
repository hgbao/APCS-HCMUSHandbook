package com.hgbao.hcmushandbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;
import com.hgbao.sensor.ShakeDetector;
import com.hgbao.thread.TaskFacebookLogin;
import com.hgbao.thread.TaskGCMRegister;
import com.hgbao.thread.TaskGCMUnregister;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MainActivity extends ActionBarActivity{

    DrawerLayout layoutDrawer;
    NavigationView navigationView;
    android.support.v7.widget.Toolbar supportToolbar;

    LinearLayout layoutFacebook;
    LoginButton btnLogin;
    CallbackManager manager;
    ImageView imgAvatar, imgCover;
    TextView txtUsername;

    LinearLayout layoutActivity, layoutScholarship, layoutEntertainment;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(MainActivity.this);
        FacebookSdk.setApplicationId(DataProvider.FACEBOOK_APPLICATION_ID);
        manager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        addControl();
        initilizeControlData();
        addEvent();
    }

    private void addControl(){
        //Navigation drawer
        layoutDrawer = (DrawerLayout) findViewById(R.id.main_nav_drawer);
        supportToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        //Layout facebook
        layoutFacebook = (LinearLayout) findViewById(R.id.layoutFacebook);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        btnLogin = (LoginButton) findViewById(R.id.btnLogin);
        //Tool bar
        if(supportToolbar != null) {
            setSupportActionBar(supportToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            supportToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        }
        //Shake detector
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        //Controls
        layoutActivity = (LinearLayout) findViewById(R.id.layoutActivity);
        layoutScholarship = (LinearLayout) findViewById(R.id.layoutScholarship);
        layoutEntertainment = (LinearLayout) findViewById(R.id.layoutEntertainment);
    }

    private void initilizeControlData(){
        //Navigation drawer
        if (!SupportProvider.isNetworkConnected(MainActivity.this)) {
            navigationView.getMenu().findItem(R.id.main_notification).setEnabled(false);
            navigationView.getMenu().findItem(R.id.main_logout).setEnabled(false);
        }
        else{
            if (DataProvider.isReceiveNoti(MainActivity.this))
                SupportProvider.checkItem(navigationView.getMenu().findItem(R.id.main_notification));
        }
        //Layout facebook
        if (DataProvider.isLogin(MainActivity.this))
            handleFacebook(true);
        else
            handleFacebook(false);

    }

    private void addEvent(){
        //NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.main_logout: {
                        LoginManager.getInstance().logOut();
                        DataProvider.logout(MainActivity.this);
                        handleFacebook(false);
                        break;
                    }
                    case R.id.main_notification: {
                        if (menuItem.isChecked())
                            new TaskGCMUnregister(MainActivity.this, menuItem).execute();
                        else
                            new TaskGCMRegister(MainActivity.this, menuItem).execute();
                        break;
                    }
                    case R.id.main_exit:
                        finish();
                        break;
                }
                return false;
            }
        });
        //Toolbar
        supportToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDrawer.openDrawer(GravityCompat.START);
            }
        });
        //Shake detector
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                handleShakeEvent(count);
            }
        });
        //Facebook login button
        btnLogin = (LoginButton) findViewById(R.id.btnLogin);
        btnLogin.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                TaskFacebookLogin task = new TaskFacebookLogin(MainActivity.this,
                        loginResult.getAccessToken().getUserId(), loginResult.getAccessToken().getToken());
                try {
                    task.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                handleFacebook(true);
                Toast.makeText(MainActivity.this, getString(R.string.message_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, getString(R.string.message_cancel), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleShakeEvent(int count){
        if (count != 0) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(DataProvider.TIME_VIBRATION);
            if (layoutDrawer.isDrawerOpen(GravityCompat.START))
                layoutDrawer.closeDrawer(GravityCompat.START);
            else
                layoutDrawer.openDrawer(GravityCompat.START);
        }
    }

    private void handleFacebook(boolean isLogin){
        if (isLogin){
            layoutFacebook.setVisibility(View.VISIBLE);
            imgCover.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            navigationView.getMenu().setGroupVisible(R.id.main_group_facebook, true);
            txtUsername.setText(DataProvider.getUsername(MainActivity.this));
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/utm_facebook.ttf");
            txtUsername.setTypeface(type);
            //Image for avatar and cover
            byte[] avatar = DataProvider.getAvatar(MainActivity.this);
            if (avatar != null)
                imgAvatar.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(avatar)));
            byte[] cover = DataProvider.getCover(MainActivity.this);
            if (cover != null)
                imgCover.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(cover)));
        }
        else{
            layoutFacebook.setVisibility(View.GONE);
            imgCover.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            navigationView.getMenu().setGroupVisible(R.id.main_group_facebook, false);
        }
    }

    public void layoutClicked(View v){
        if (v.getId() == R.id.layoutOther){
            Intent i = new Intent(MainActivity.this, OtherActivity.class);
            startActivity(i);
        }
        else {
            Intent i = new Intent(MainActivity.this, OptionListActivity.class);
            switch (v.getId()) {
                case R.id.layoutActivity:
                    i.putExtra(DataProvider.EXTRA_OPTION_TYPE, DataProvider.EXTRA_ACTIVITY);
                    break;
                case R.id.layoutScholarship:
                    i.putExtra(DataProvider.EXTRA_OPTION_TYPE, DataProvider.EXTRA_SCHOLARSHIP);
                    break;
                case R.id.layoutEntertainment:
                    i.putExtra(DataProvider.EXTRA_OPTION_TYPE, DataProvider.EXTRA_ENTERTAINMENT);
                    break;
            }
            startActivity(i);
            overridePendingTransition(R.anim.activity_slide_left_1, R.anim.activity_slide_left_2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
