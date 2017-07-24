package com.hgbao.hcmushandbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;
import com.hgbao.thread.TaskDatabaseCheck;


public class LoadingActivity extends ActionBarActivity {

    TextView txtProgress;
    ImageView imgAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        addControl();
        startLoading();
    }

    private void addControl() {
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        imgAnimation = (ImageView) findViewById(R.id.imgLoading);
        //Add fading animation
        final Animation fade_in = new AlphaAnimation(0f, 0.8f);
        final Animation fade_out = new AlphaAnimation(0.8f, 0f);
        fade_in.setDuration(DataProvider.TIME_ANIMATION_FADING);
        fade_out.setDuration(DataProvider.TIME_ANIMATION_FADING);
        //Set animation listener to sequence the animations
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtProgress.startAnimation(fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtProgress.startAnimation(fade_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //Start animation
        txtProgress.startAnimation(fade_out);
        imgAnimation.setBackgroundResource(R.drawable.drawable_anim_pusheen);
        AnimationDrawable draw = (AnimationDrawable) imgAnimation.getBackground();
        draw.start();
    }

    private void startLoading() {
        //Read update first
        DataProvider.createDatabase(LoadingActivity.this);
        DataProvider.readUpdate();
        if (SupportProvider.isNetworkConnected(LoadingActivity.this)) {
            TaskDatabaseCheck task = new TaskDatabaseCheck(LoadingActivity.this, txtProgress);
            task.execute();
        }
        else
            aleartNoNetwork();
    }

    private void aleartNoNetwork(){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
            //Message and title
            builder.setTitle("Network error");
            builder.setMessage(getString(R.string.message_error_network));
            builder.setCancelable(false);
            //Buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataProvider.readDatabase();
                    Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (SupportProvider.isNetworkConnected(LoadingActivity.this)){
                        TaskDatabaseCheck task = new TaskDatabaseCheck(LoadingActivity.this, txtProgress);
                        task.execute();
                    }
                    else
                        aleartNoNetwork();
                }
            });
            builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
