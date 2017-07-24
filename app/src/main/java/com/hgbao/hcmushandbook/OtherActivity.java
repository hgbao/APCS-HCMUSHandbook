package com.hgbao.hcmushandbook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.hgbao.provider.DataProvider;


public class OtherActivity extends ActionBarActivity {

    ImageButton btnBus, btnFacebook, btnCall, btnMail, btnWeb, btnDoan;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        addControl();
        addEvent();
    }

    private void addControl(){
        //Action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Control
        btnBus = (ImageButton) findViewById(R.id.btnBus);
        btnFacebook = (ImageButton) findViewById(R.id.btnFacebook);
        btnCall = (ImageButton) findViewById(R.id.btnCall);
        btnMail = (ImageButton) findViewById(R.id.btnMail);
        btnWeb = (ImageButton) findViewById(R.id.btnWeb);
        btnDoan = (ImageButton) findViewById(R.id.btnDoan);
    }

    private void addEvent(){
        btnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtherActivity.this, BusActivity.class);
                startActivity(i);
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(DataProvider.DATA_URL_FANPAGE));
                startActivity(i);
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + DataProvider.DATA_PHONE_NUMBER));
                startActivity(intent);
            }
        });
        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("text/plain");
                i.setData(Uri.parse("mailto:" + DataProvider.DATA_EMAIL));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(DataProvider.DATA_URL_WEBSITE));
                startActivity(i);
            }
        });
        btnDoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(DataProvider.DATA_URL_DOANTN));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other, menu);
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
