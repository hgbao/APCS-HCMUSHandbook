package com.hgbao.hcmushandbook;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hgbao.model.Entertainment;
import com.hgbao.provider.DataProvider;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;


public class EntertainmentActivity extends ActionBarActivity {

    Entertainment entertainment;
    TextView txtName, txtType, txtVote, txtDescription, txtAddress, txtWebsite;
    ImageView imgAvatar;
    RatingBar ratingBar;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);

        String eID = getIntent().getStringExtra(DataProvider.EXTRA_ITEM);
        int n = DataProvider.list_entertainment.size();
        for (int i = 0; i < n; i++) {
            Entertainment cur = DataProvider.list_entertainment.get(i);
            if (cur.getId().equalsIgnoreCase(eID)){
                entertainment = cur;
                break;
            }
        }

        addControl();
        initializeData();
        addEvent();
    }

    private void addControl(){
        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Controls
        imgAvatar = (ImageView) findViewById(R.id.imgDetailEntertainment);
        ratingBar = (RatingBar) findViewById(R.id.ratingDetailEntertainment);
        txtName = (TextView) findViewById(R.id.txtDetailEntertainmentName);
        txtType = (TextView) findViewById(R.id.txtDetailEntertainmentType);
        txtVote = (TextView) findViewById(R.id.txtDetailEntertainmentVote);
        txtDescription = (TextView) findViewById(R.id.txtDetailEntertainmentDescription);
        txtAddress = (TextView) findViewById(R.id.txtDetailEntertainmentAddress);
        txtWebsite = (TextView) findViewById(R.id.txtDetailEntertainmentWebsite);
        //Underline link text
        txtAddress.setPaintFlags(txtAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtWebsite.setPaintFlags(txtWebsite.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void addEvent(){
        txtWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(entertainment.getWebsite()));
                startActivity(i);
            }
        });
        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EntertainmentActivity.this, MapActivity.class);
                i.putExtra(DataProvider.EXTRA_ITEM, entertainment.getId());
                startActivity(i);
            }
        });
    }

    /**
     * Initializing data for controls
     */
    private void initializeData(){
        //Information
        imgAvatar.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(entertainment.getAvatar())));
        ratingBar.setRating(entertainment.getRating());
        ratingBar.setClickable(false);
        txtName.setText(entertainment.getName());
        txtDescription.setText(entertainment.getDescription());
        txtAddress.setText(entertainment.getAddress());
        txtWebsite.setText(entertainment.getName());
        txtVote.setText("(" + entertainment.getVote() + " votes)");
        switch (entertainment.getType()){
            case 1:
                txtType.setText(getString(R.string.filter_ent_type1));
                break;
            case 2:
                txtType.setText(getString(R.string.filter_ent_type2));
                break;
            case 3:
                txtType.setText(getString(R.string.filter_ent_type3));
        }
        //Google map
        if (map == null){
            map = ((MapFragment)getFragmentManager().findFragmentById(R.id.mapEntertainment)).getMap();
            if (map != null){
                LatLng position = new LatLng(entertainment.getLatitude(), entertainment.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DataProvider.MAP_ZOOM_CURRENT));
                map.getUiSettings().setAllGesturesEnabled(false);
                //Marker for place
                MarkerOptions opt = new MarkerOptions();
                opt.title(entertainment.getName());
                opt.position(position);
                Marker marker = map.addMarker(opt);
                marker.showInfoWindow();
            }
            else
                Toast.makeText(EntertainmentActivity.this, getString(R.string.message_error_map), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entertainment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
