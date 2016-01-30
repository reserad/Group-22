package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alec Reser on 1/28/2016.
 */
public class Navigation extends Fragment implements TabLayout.OnTabSelectedListener{
    private MapView mapView = null;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        Button search = (Button)view.findViewById(R.id.search);
        final EditText input = (EditText)view.findViewById(R.id.input);
        mapView = (MapView) view.findViewById(R.id.mapview);
        search.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                LatLng lat = getLocationFromAddress(view.getContext(), input.getText().toString());
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(view.getContext(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(lat.getLatitude(), lat.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    mapView.addMarker(new Marker(mapView, address, city + ", " + state, lat));
                    mapView.setCenter(lat);
                    mapView.setZoom(14);
                }
                catch(IOException e){
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapView.setVisibility(View.VISIBLE);
        return view;
    }
    public void onTabSelected(final TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(final TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(final TabLayout.Tab tab) {

    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}
