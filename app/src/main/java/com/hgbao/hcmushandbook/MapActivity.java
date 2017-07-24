package com.hgbao.hcmushandbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hgbao.model.Entertainment;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;
import com.hgbao.thread.TaskGetDirection;

import java.util.ArrayList;

public class MapActivity extends ActionBarActivity {
    Entertainment entertainment;
    LatLng positionHCMUS, positionPlace, positionMy;

    ActionBar actionBar;
    ProgressDialog dialog;

    GoogleMap map;
    ArrayList<LatLng> listStep;
    PolylineOptions polyline;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String eID = getIntent().getStringExtra(DataProvider.EXTRA_ITEM);
        if (!eID.isEmpty()) {
            int n = DataProvider.list_entertainment.size();
            for (int i = 0; i < n; i++) {
                Entertainment cur = DataProvider.list_entertainment.get(i);
                if (cur.getId().equalsIgnoreCase(eID)) {
                    entertainment = cur;
                    break;
                }
            }
        }
        positionHCMUS = new LatLng(DataProvider.UNIVERSITY_LATITUDE, DataProvider.UNIVERSITY_LONGITUDE);
        positionPlace = new LatLng(entertainment.getLatitude(), entertainment.getLongitude());

        addControl();
    }

    private void addControl() {
        //Action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Google map
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapEntertainment)).getMap();
            if (map != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(positionPlace, DataProvider.MAP_ZOOM_CURRENT));
                map.getUiSettings().setAllGesturesEnabled(true);
                //Marker for place
                MarkerOptions optPlace = new MarkerOptions();
                optPlace.title(entertainment.getName())
                        .position(positionPlace)
                        .snippet(entertainment.getAddress());
                map.addMarker(optPlace).showInfoWindow();
                //Marker for university
                MarkerOptions optUniversity = new MarkerOptions();
                optUniversity.title("HCMUS")
                        .position(positionHCMUS);
                map.addMarker(optUniversity).showInfoWindow();
                polyline = new PolylineOptions();
            } else
                Toast.makeText(MapActivity.this, getString(R.string.message_error_map), Toast.LENGTH_LONG).show();
        }
        //Location manager
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
    }

    private void currentLocation() {
        Location lastLocation = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (lastLocation != null)
        {
            positionMy = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(positionMy, DataProvider.MAP_ZOOM_CURRENT));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(positionMy)
                    .zoom(DataProvider.MAP_ZOOM_CURRENT)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //Add MarketOption
            MarkerOptions option = new MarkerOptions();
            option.title("You are here");
            option.position(positionMy);
            Marker currentMarker= map.addMarker(option);
            currentMarker.showInfoWindow();
        }
    }

    private void showDirection(final LatLng origin, final LatLng desitination){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listStep = new ArrayList<LatLng>();
                dialog = new ProgressDialog(MapActivity.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                TaskGetDirection task = new TaskGetDirection();
                ArrayList<LatLng> list = task.getDirection(origin, desitination);
                for (LatLng latLng : list) {
                    listStep.add(latLng);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                polyline.addAll(listStep);
                Polyline line = map.addPolyline(polyline);
                line.setColor(Color.RED);
                line.setWidth(5);
                //Zoom between 2 points
                LatLng positionBetween =
                        new LatLng((origin.latitude + desitination.latitude)/2, (origin.longitude + desitination.longitude)/2);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(positionBetween, DataProvider.MAP_ZOOM_CURRENT));

                dialog.dismiss();
            }
        };
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        if (!SupportProvider.isNetworkConnected(MapActivity.this)){
            menu.findItem(R.id.map_direction).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.map_mylocation: {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    currentLocation();
                else
                    Toast.makeText(MapActivity.this, getString(R.string.message_error_gps), Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.map_direction:
                if (SupportProvider.isNetworkConnected(MapActivity.this))
                    showDirection(positionHCMUS, positionPlace);
                else
                    Toast.makeText(MapActivity.this, getString(R.string.message_error_network), Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
