package com.hgbao.thread;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.hgbao.model.Direction;
import com.hgbao.provider.DataProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TaskGetDirection {
    public ArrayList<LatLng> getDirection(LatLng origin, LatLng destination)
    {
        try {
            ArrayList<LatLng> ret = new ArrayList<LatLng>();
            double oriLat = origin.latitude;
            double oriLong = origin.longitude;
            double desLat = destination.latitude;
            double desLong = destination.longitude;
            //API Key here is the Browser Key (Not Android Key)
            String address = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + oriLat + ",%20" + oriLong +
                    "&destination=" + desLat + ",%20" + desLong
                    + "&key=" + DataProvider.GOOGLE_BROWSWER_KEY;
            URL url;
            url = new URL(address);

            InputStreamReader reader = new InputStreamReader(url.openStream(),"UTF-8");

            Direction results = new Gson().fromJson(reader, Direction.class);
            Direction.Route[] routes = results.getRoutes();
            Direction.Leg[] leg = routes[0].getLegs();
            Direction.Leg.Step[] steps = leg[0].getSteps();
            for (Direction.Leg.Step step : steps) {
                LatLng latlngStart = new LatLng(step.getStart_location().getLat(), step.getStart_location().getLng());
                LatLng latlngEnd = new LatLng(step.getEnd_location().getLat(), step.getEnd_location().getLng());
                ret.add(latlngStart);
                ret.add(latlngEnd);
            }

            return ret;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
