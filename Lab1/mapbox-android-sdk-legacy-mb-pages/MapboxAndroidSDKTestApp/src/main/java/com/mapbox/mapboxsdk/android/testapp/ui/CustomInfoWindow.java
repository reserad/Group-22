package com.mapbox.mapboxsdk.android.testapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CustomInfoWindow extends InfoWindow {
    
    public CustomInfoWindow(final MapView mv, final LatLng navigateTo) {
        super(R.layout.infowindow_custom, mv);
        
        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Geocoder geocoder;
                    String bestProvider;
                    List<Address> user;
                    user = null;
                    double lat;
                    double lng;

                    LocationManager lm = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);

                    Criteria criteria = new Criteria();
                    bestProvider = lm.getBestProvider(criteria, false);
                    Location location = lm.getLastKnownLocation(bestProvider);

                    if (location == null){
                        lat = 39.1321095;
                        lng = -84.5177543;
                    }else {
                        geocoder = new Geocoder(v.getContext());
                        try {
                            user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            lat = (double) user.get(0).getLatitude();
                            lng = (double) user.get(0).getLongitude();
                            //navigateTo
                            //https://api.mapbox.com/v4/directions/mapbox.driving/39.1321095,-84.5177543;39.598322,-84.1787949.json?access_token=pk.eyJ1IjoicmVzZXJhZCIsImEiOiJjaWs4dzdubWgwMHhvdXhrdXN2eTd5djVoIn0.nTcJFOD8ofmioyrjiADLRA
                            String sURL = "https://api.mapbox.com/v4/directions/mapbox.driving/" + lng + "," + lat + ";" + navigateTo.getLongitude() + "," + navigateTo.getLatitude() + ".json?access_token=pk.eyJ1IjoicmVzZXJhZCIsImEiOiJjaWs4dzdubWgwMHhvdXhrdXN2eTd5djVoIn0.nTcJFOD8ofmioyrjiADLRA";
                            // Connect to the URL using java's native library
                            URL url = new URL(sURL);
                            HttpURLConnection request = (HttpURLConnection) url.openConnection();
                            request.connect();
                            Toast.makeText(v.getContext(), "hit", Toast.LENGTH_LONG).show();
                            JsonParser jp = new JsonParser();
                            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                            JsonArray j = root.getAsJsonObject().getAsJsonArray("routes").getAsJsonObject().getAsJsonObject("geometry").getAsJsonArray("coordinates");
                            Toast.makeText(v.getContext(), "hit2", Toast.LENGTH_LONG).show();

                            PathOverlay pathOverlay  = new PathOverlay();
                            pathOverlay.getPaint().setStyle(Paint.Style.STROKE);
                            for(final JsonElement type : j) {
                                JsonArray coords = type.getAsJsonArray();
                                pathOverlay.addPoint(Double.parseDouble(coords.get(0).toString()), Double.parseDouble(coords.get(1).toString()));
                            }
                            Toast.makeText(v.getContext(), "hit3", Toast.LENGTH_LONG).show();

                            mv.addOverlay(pathOverlay);

                        } catch (Exception e) {
                            Toast.makeText(v.getContext(), "error", Toast.LENGTH_LONG).show();
                    }
                    }
                    //https://api.mapbox.com/geocoding/v5/mapbox.places/.json?access_token=pk.eyJ1IjoicmVzZXJhZCIsImEiOiJjaWs4c214bm8wMDNldXRrb2duMHMxZmhzIn0.IA1KJeoaW33Fhkbw42YJ8Q
                    //pk.eyJ1IjoicmVzZXJhZCIsImEiOiJjaWs4dzdubWgwMHhvdXhrdXN2eTd5djVoIn0.nTcJFOD8ofmioyrjiADLRA
                }
                // Return true as we're done processing this event
                return true;
            }
        });
    }

    /**
     * Dynamically set the content in the CustomInfoWindow
     * @param overlayItem The tapped Marker
     */
    @Override
    public void onOpen(Marker overlayItem) {
        String title = overlayItem.getTitle();
        ((TextView) mView.findViewById(R.id.customTooltip_title)).setText(title);

        String description = overlayItem.getDescription();
        ((TextView) mView.findViewById(R.id.customTooltip_Description)).setText(description);
    }
}
