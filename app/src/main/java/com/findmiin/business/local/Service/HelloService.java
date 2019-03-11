package com.findmiin.business.local.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceReport;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by JonIC on 2017-04-06.
 */

public class HelloService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String country = "";
    String state = "";
    String city ="";
    String address="";
    Context context;


    static boolean serviceRunning = false;
    static int INTERVAL = 1000 * 30 * 1;
    static boolean serviceSt = true;


    @Override
    public void onCreate() {

        context = this;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
          if(!serviceRunning){
//              Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
              oneService();
          }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        serviceSt = true;
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.disconnect();
    }
    private  void oneTasks() {
        //task itself
        try{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
            if (mLastLocation != null) {
                String user_id = DataUtils.getPreference(Const.USER_ID,"");
                String longintude = String.valueOf(mLastLocation.getLongitude());
                String latitude = String.valueOf(mLastLocation.getLatitude());

//              latitude ="40.054054054054056";
//              longintude = "124.3120835140237";

                double lat = Float.parseFloat(latitude);
                double lon = Float.parseFloat(longintude);
                getAddress(lat,lon);
                DataUtils.savePreference(Const.LATITUDE, latitude);
                DataUtils.savePreference(Const.LONGITUDE, longintude);
                if(checkLocationChange(longintude,latitude) && !user_id.equals("")){

                }
                String userid = DataUtils.getPreference(Const.USER_ID,"");
                String login_type = DataUtils.getPreference(Const.LOGIN_TYPE, "client");

                if(user_id != "" && login_type.equals("user")){
                    ServerManager.locationUpdate(latitude, longintude, userid, new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            if(result.mResult == LogicResult.RESULT_OK){
//                                Toast.makeText(context , "success", Toast.LENGTH_SHORT).show();
                            }else{
//                                Toast.makeText(context , "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }catch (SecurityException e){

        }
    }

    private boolean checkLocationChange(String longitude, String latitude){

        if (country != null && !country.isEmpty() && !country.equals("null")){
        }else {
            country = "";
        }
        if (city != null && !city.isEmpty() && !city.equals("null")){
        }else{
            city = "";
        }
        if (state != null && !state.isEmpty() && !state.equals("null")){
        }else{
            state = "";
        }
        if (address != null && !address.isEmpty() && !address.equals("null")){
        }else{
            address = "";
        }

        String old_longitude = DataUtils.getPreference(Const.LONGITUDE,"");
        String old_latitude = DataUtils.getPreference(Const.LATITUDE,"");
        if((latitude.equals("") && longitude.equals("")) || (latitude.equals("0")&& longitude.equals("0"))){
            return false;
        }
        if(old_latitude.equals(latitude) && old_longitude.equals(longitude)){
            return false;
        }else{
            return true;
        }
    }


    private  void oneService() {
        if (!serviceSt) {
            serviceRunning = false;
            return;
        }
        serviceRunning = true;
        oneTasks();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                oneService();
            }
        }, (INTERVAL));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
//                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }catch (SecurityException e){

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult e){

    }

    @Override
    public void onConnectionSuspended(int result){

    }
    private void getAddress(double latitude, double longitude){

//        latitude = Double.parseDouble("40.054054054054056");
//        longitude = Double.parseDouble("124.3120835140237");
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try{

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if(addresses.size() > 0){
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                DataUtils.savePreference(Const.CITY, city);
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
            }
        }catch (IOException e3){
            String error = e3.toString();
        }
    }

}