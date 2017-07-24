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

import com.hgbao.model.Scholarship;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;

import java.util.Calendar;


public class ScholarshipActivity extends ActionBarActivity {

    Scholarship scholarship;
    TextView txtName, txtAbout, txtType;
    TextView txtDeadline, txtReceive, txtCreated;
    Button btnSubmit, btnDeadline, btnReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scholarship = (Scholarship) getIntent().getSerializableExtra(DataProvider.EXTRA_ITEM);
        if (scholarship.getType() == 4)
            handleResult();
        else {
            addControl();
            initializeData();
            addEvent();
        }
    }

    private void addControl(){
        setContentView(R.layout.activity_scholarship);
        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Controls
        txtName = (TextView) findViewById(R.id.txtDetailScholarshipName);
        txtType = (TextView) findViewById(R.id.txtDetailScholarshipType);
        txtAbout = (TextView) findViewById(R.id.txtDetailScholarshipAbout);
        txtCreated = (TextView) findViewById(R.id.txtDetailScholarshipDate);
        txtDeadline = (TextView) findViewById(R.id.txtDetailScholarshipDeadline);
        txtReceive = (TextView) findViewById(R.id.txtDetailScholarshipReceive);
        btnSubmit = (Button) findViewById(R.id.btnScholarshipSummit);
        btnDeadline = (Button) findViewById(R.id.btnRemindDeadline);
        btnReceive = (Button) findViewById(R.id.btnRemindReceive);
        //Underline link text
        txtAbout.setPaintFlags(txtAbout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void addEvent(){
        txtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(scholarship.getAbout()));
                startActivity(i);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, scholarship.getSubmitSubject());
                i.setData(Uri.parse("mailto:" + scholarship.getSubmitEmail()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        btnDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminder(0);
            }
        });
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminder(1);
            }
        });
    }

    /**
     * Reformat the layout if this is an announcement or result news
     */
    private void handleResult(){
        setContentView(R.layout.activity_scholarship_result);
        //Controls
        txtName = (TextView) findViewById(R.id.txtResultScholarshipName);
        txtAbout = (TextView) findViewById(R.id.txtResultScholarshipAbout);
        txtAbout.setPaintFlags(txtAbout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtCreated = (TextView) findViewById(R.id.txtResultScholarshipDate);
        txtType = (TextView) findViewById(R.id.txtResultScholarshipType);
        Button btnResult = (Button) findViewById(R.id.btnScholarshipResult);
        txtType.setText(getString(R.string.filter_act_type4));
        //Data
        txtName.setText(scholarship.getName());
        txtAbout.setText(scholarship.getName());
        txtCreated.setText("(" + SupportProvider.stringDate(scholarship.getDateCreated()) + ")");
        //Events
        txtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(scholarship.getAbout()));
                startActivity(i);
            }
        });
        if (scholarship.getSubmitEmail().isEmpty()){
            btnResult.setText(getString(R.string.detail_scholarship_result_error));
            btnResult.setEnabled(false);
        }
    }

    /**
     * Initializing data for controls
     */
    private void initializeData(){
        //Text
        txtName.setText(scholarship.getName());
        txtAbout.setText(scholarship.getName());
        switch (scholarship.getType()){
            case 1:
                txtType.setText(getString(R.string.filter_sch_type1));
                break;
            case 2:
                txtType.setText(getString(R.string.filter_sch_type2));
                break;
            case 3:
                txtType.setText(getString(R.string.filter_sch_type3));
        }

        //Date
        txtCreated.setText("(" + SupportProvider.stringDate(scholarship.getDateCreated()) + ")");
        String deadline = getString(R.string.detail_scholarship_deadline) + ": ";
        String receive = getString(R.string.detail_scholarship_receive) + ": ";
        long dateDeadline = scholarship.getDateDeadline();
        if (dateDeadline != 0)
            txtDeadline.setText(deadline + SupportProvider.stringDate(scholarship.getDateDeadline()));
        else
            txtDeadline.setText(deadline + "null");

        long dateReceive = scholarship.getDateReceive();
        if (dateReceive != 0)
            txtReceive.setText(receive + SupportProvider.stringDate(scholarship.getDateReceive()));
        else
            txtReceive.setText(receive + "null");
        //Button
        String strExpired = getString(R.string.detail_scholarship_expired);
        if (scholarship.isExpired()){
            btnSubmit.setText(strExpired);
            btnDeadline.setText(strExpired);
            btnSubmit.setEnabled(false);
            btnDeadline.setEnabled(false);
        }
        else {
            if (scholarship.getSubmitEmail().isEmpty()) {
                btnSubmit.setEnabled(false);
                btnSubmit.setText(getString(R.string.detail_scholarship_submit_error));
            }
            if (dateDeadline == 0)
                btnDeadline.setEnabled(false);
        }
        if (dateReceive == 0)
            btnReceive.setEnabled(false);
        else{
            if (Calendar.getInstance().getTimeInMillis() > dateReceive){
                btnReceive.setText(strExpired);
                btnReceive.setEnabled(false);
            }
        }
    }

    /**
     * Add event to calendar for reminding deadline or receiving result dates
     * @param type: 0 - deadline, 1 - receive result
     */
    private void setReminder(int type){
        String strDescription;
        long time;
        if (type == 0) {
            strDescription = getString(R.string.detail_scholarship_deadline) + ": " + scholarship.getName();
            time = scholarship.getDateDeadline();
        }
        else {
            strDescription = getString(R.string.detail_scholarship_receive) + ": " + scholarship.getName();
            time = scholarship.getDateReceive();
        }
        SupportProvider.setCalendarEvent(ScholarshipActivity.this, scholarship.getName(), "", strDescription, true, time, time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scholarship, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
