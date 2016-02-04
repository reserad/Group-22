package com.mapbox.mapboxsdk.android.testapp.ui;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;

import com.mapbox.mapboxsdk.android.testapp.Navigation;
import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CustomInfoWindow extends InfoWindow {
    
    public CustomInfoWindow(final MapView mv) {
        super(R.layout.infowindow_custom, mv);
        
        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(v.getContext(), "hit", Toast.LENGTH_SHORT).show();
                    UserLocationOverlay userLocation = new UserLocationOverlay(new GpsLocationProvider(v.getContext()), mv);
                    mv.addMarker(new Marker(mv, "", "", userLocation.getMyLocation()));

                    /*Geocoder geocoder = new Geocoder(v.getContext(), Locale.getDefault());
                    LocationManager service = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = service.getBestProvider(criteria, false);
                    service.requestLocationUpdates(provider, 1000, 0, mLocationListener);
                    Location location = service.getLastKnownLocation(provider);

                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(userLocation.getLatitude(), userLocation.getLongitude(), 1);
                        mv.addMarker(new Marker(mv, addresses.get(0).getAddressLine(0), addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea(), userLocation));
                    } catch (IOException e) {
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }*/

                    close();
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

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
