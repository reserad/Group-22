package com.mapbox.mapboxsdk.android.testapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import java.io.BufferedReader;
import java.io.Reader;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class CustomInfoWindow extends InfoWindow {
    double lat, lng;
    public CustomInfoWindow(final MapView mapView, final LatLng navigateTo) {
        super(R.layout.infowindow_custom, mapView);
        TextView textView = (TextView) getView().findViewById(R.id.customTooltip_Navigate);

        textView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mapView.closeCurrentTooltip();
                Geocoder geocoder;
                String bestProvider;
                List<Address> user;
                LocationManager locationManager = (LocationManager) getView().getContext().getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                bestProvider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(bestProvider);

                geocoder = new Geocoder(getView().getContext());
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    if (location == null) {
                        lat = 39.1321095;
                        lng = -84.5177543;
                        mapView.addMarker(new Marker(mapView, "University of Cincinnati", "Cincinnati" + ", " + "Ohio", new LatLng(lat, lng)));
                    } else {
                        user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        lat = (double) user.get(0).getLatitude();
                        lng = (double) user.get(0).getLongitude();
                        mapView.addMarker(new Marker(mapView, user.get(0).getAddressLine(0), user.get(0).getLocality() + ", " + user.get(0).getAdminArea(), new LatLng(lat, lng)));
                    }

                    String sURL = "https://api.mapbox.com/v4/directions/mapbox.driving/" + lng + "," + lat + ";" + navigateTo.getLongitude() + "," + navigateTo.getLatitude() + ".json?access_token=pk.eyJ1IjoicmVzZXJhZCIsImEiOiJjaWs4dzdubWgwMHhvdXhrdXN2eTd5djVoIn0.nTcJFOD8ofmioyrjiADLRA";
                    JSONObject jsonObject = getJsonObject(sURL);
                    displayRoutes(mapView, getView().getContext(), jsonObject);

                } catch (IOException | JSONException e) {
                    Toast.makeText(getView().getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private JSONObject getJsonObject(String sURL) throws IOException, JSONException{
        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestMethod("GET");
        request.setRequestProperty("Content-length", "0");
        request.setUseCaches(false);
        request.setAllowUserInteraction(false);
        request.connect();
        return readJsonFromUrl(sURL);
    }

    private void displayRoutes(final MapView mapView, final Context context, final JSONObject jsonObject) {
        View promptsView = View.inflate(context, R.layout.fragment_choose_route, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        double metersToMilesMultiplier = 0.000621371;
        try {
            LinearLayout buttonLayout = (LinearLayout) promptsView.findViewById(R.id.buttonLayout);
            for (int i = 0; i < jsonObject.getJSONArray("routes").length(); i++) {
                double distance = Double.parseDouble(jsonObject.getJSONArray("routes").getJSONObject(i).get("distance").toString());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout nested_li = new LinearLayout(context);
                nested_li.setOrientation(LinearLayout.HORIZONTAL);
                nested_li.setLayoutParams(lp);
                nested_li.setPadding(5, 5, 5, 5);

                TextView tv = new TextView(context);
                tv.setText("Route " + (i + 1));
                tv.setTextColor(Color.WHITE);
                tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                nested_li.addView(tv);

                TextView tvMiles = new TextView(context);
                tvMiles.setText(roundToNDecimalPlaces(distance * metersToMilesMultiplier, 1) + " miles");
                tvMiles.setTextColor(Color.WHITE);
                tvMiles.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                nested_li.addView(tvMiles);

                Button myButton = new Button(context);
                myButton.setBackgroundResource(R.color.mapboxGreen);
                myButton.setText("SELECT");
                myButton.setTextColor(Color.WHITE);
                myButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                nested_li.addView(myButton);

                buttonLayout.addView(nested_li);
                myButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (Overlay overlay : mapView.getOverlays()) {
                            mapView.removeOverlay(overlay);
                        }
                        drawLines(mapView, context, jsonObject);
                        alertDialog.dismiss();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            stringBuilder.append((char) cp);
        }
        return stringBuilder.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(bufferedReader);
            return new JSONObject(jsonText);
        } finally {
            inputStream.close();
        }
    }

    private void drawLines(MapView mapView, Context context, JSONObject jsonObject) {
        try {
            PathOverlay pathOverlay = new PathOverlay();
            pathOverlay.getPaint().setStyle(Paint.Style.STROKE);

            JSONArray jarray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
            for (int i = 0; i < jarray.length(); i++) {
                JSONArray jo = jarray.getJSONArray(i);
                pathOverlay.addPoint(Double.parseDouble(jo.get(1).toString()), Double.parseDouble(jo.get(0).toString()));
            }

            pathOverlay.getPaint().setColor(Color.BLUE);
            mapView.addOverlay(pathOverlay);

            mapView.setZoom(14);
            mapView.setCenter(new LatLng(lat, lng));

        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static double roundToNDecimalPlaces(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
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