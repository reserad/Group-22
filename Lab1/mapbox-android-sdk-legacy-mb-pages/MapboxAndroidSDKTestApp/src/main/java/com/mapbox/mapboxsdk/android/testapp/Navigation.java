package com.mapbox.mapboxsdk.android.testapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alec Reser on 1/28/2016.
 */
public class Navigation extends Fragment implements TabLayout.OnTabSelectedListener{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
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
}
