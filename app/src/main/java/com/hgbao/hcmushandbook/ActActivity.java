package com.hgbao.hcmushandbook;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hgbao.model.Act;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;

import java.util.Calendar;


public class ActActivity extends ActionBarActivity {

    Act activity;
    TextView txtName, txtAbout, txtType;
    TextView txtCreated, txtOccur;
    Button btnSubmit, btnOccur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);

        activity = (Act) getIntent().getSerializableExtra(DataProvider.EXTRA_ITEM);

        addControl();
        initializeData();
        addEvent();
        //Handling for result news
        if (activity.getType() == 4)
            handleResult();
    }

    private void addControl(){
        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Controls
        txtName = (TextView) findViewById(R.id.txtDetailActivityName);
        txtType = (TextView) findViewById(R.id.txtDetailActivityType);
        txtAbout = (TextView) findViewById(R.id.txtDetailActivityAbout);
        txtCreated = (TextView) findViewById(R.id.txtDetailActivityDate);
        txtOccur = (TextView) findViewById(R.id.txtDetailActivityOccur);
        btnSubmit = (Button) findViewById(R.id.btnActivitySummit);
        btnOccur = (Button) findViewById(R.id.btnRemindOccur);
        //Underline link text
        txtAbout.setPaintFlags(txtAbout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initializeData() {
        //Text
        txtName.setText(activity.getName());
        txtAbout.setText(activity.getName());
        switch (activity.getType()) {
            case 1:
                txtType.setText(getString(R.string.filter_act_type1));
                break;
            case 2:
                txtType.setText(getString(R.string.filter_act_type2));
                break;
            case 3:
                txtType.setText(getString(R.string.filter_act_type3));
                break;
            case 4:
                txtType.setText(getString(R.string.filter_act_type4));
        }
        //Date
        txtCreated.setText("(" + SupportProvider.stringDate(activity.getDateCreated()) + ")");
        //Submit
        String strExpired = getString(R.string.detail_scholarship_expired);
        if (activity.isExpired()) {
            btnSubmit.setText(strExpired);
            btnSubmit.setEnabled(false);
        } else {
            if (activity.getSubmit().isEmpty()) {
                btnSubmit.setEnabled(false);
                btnSubmit.setText(getString(R.string.detail_scholarship_submit_error));
            }
        }
        //Reminder
        long dateOccur = activity.getDateOccur();
        if (dateOccur == 0) {
            btnOccur.setEnabled(false);
            txtOccur.setText("");
        }
        else {
            if (Calendar.getInstance().getTimeInMillis() > dateOccur) {
                btnOccur.setText(strExpired);
                btnOccur.setEnabled(false);
                txtOccur.setText("(" + SupportProvider.stringDate(activity.getDateOccur()) + ")");
            }
        }
    }

    private void addEvent(){
        txtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(activity.getAbout()));
                startActivity(i);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String submit = activity.getSubmit();
                if (submit.contains("//")) {//Submit by link
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(activity.getSubmit()));
                    startActivity(i);
                }
                else{//Submit by email
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, activity.getSubmit());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });
        btnOccur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDescription;
                long time = activity.getDateOccur();
                strDescription = getString(R.string.detail_activity_occur) + ": " + activity.getName();
                SupportProvider.setCalendarEvent(ActActivity.this, activity.getName(), "", strDescription, true, time, time);
            }
        });
    }

    private void handleResult(){
        //Hide controls
        TextView txtTitle = (TextView) findViewById(R.id.textViewActRemind);
        txtTitle.setVisibility(View.GONE);
        btnOccur.setVisibility(View.GONE);
        txtOccur.setVisibility(View.GONE);
        //Events
        btnSubmit.setText(getString(R.string.detail_scholarship_result_button));
        if (activity.getSubmit().isEmpty()) {
            btnSubmit.setText(getString(R.string.detail_scholarship_result_error));
            btnSubmit.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act, menu);
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
