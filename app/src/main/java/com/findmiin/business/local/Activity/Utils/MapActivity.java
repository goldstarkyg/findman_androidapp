package com.findmiin.business.local.Activity.Utils;

import android.os.Bundle;
import android.view.KeyEvent;

import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by JonIC on 2017-06-20.
 */

public class MapActivity extends BaseActivity
        implements OnMapReadyCallback {

    String lat;
    String lon;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MYActivityManager.getInstance().pushActivity(MapActivity.this);

        Bundle intent = getIntent().getExtras();
        lat = intent.getString("lat");
        lon = intent.getString("lon");
        address = intent.getString("address");
        // Retrieve the content view that renders the map.
        setContentView(R.layout.map_layout);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        float latitude = Float.parseFloat(lat);
        float longitude = Float.parseFloat(lon);
        LatLng sydney = new LatLng(latitude, longitude);
        MarkerOptions maker = new MarkerOptions().position(sydney)
                .title(address);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);

        maker.icon(icon);
        googleMap.addMarker(maker);


        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15)); // max 21
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            MYActivityManager.getInstance().popActivity();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
