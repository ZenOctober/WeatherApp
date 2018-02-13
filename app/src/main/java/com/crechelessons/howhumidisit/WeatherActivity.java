package com.crechelessons.howhumidisit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crechelessons.howhumidisit.common.Common;
import com.crechelessons.howhumidisit.common.HelperClass;
import com.crechelessons.howhumidisit.model.OpenWeatherMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int UPDATE_INTERVAL = 10000;
    private static final int DISPLACEMENT = 50;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private SectionsPagerAdapter adapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private double lat, lng;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.SYSTEM_ALERT_WINDOW
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mViewPager = findViewById(R.id.container);
        setUpViewPager(mViewPager);
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void displayLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location!=null){
                                    lat = location.getLatitude();
                                    lng = location.getLongitude();
                                }else{
                                    Toast.makeText(WeatherActivity.this, "No location detected.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(WeatherActivity.this, permissions, 10);
            }
        } else {
            ActivityCompat.requestPermissions(WeatherActivity.this, permissions, 10);
        }
        new GetWeather().execute(Common.apiRequest(lat, lng));
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                api.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (checkPlayServices()) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API).build();
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
                displayLocation();
            }
        } else {
            final AlertDialog.Builder dialog;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            } else {
                dialog = new AlertDialog.Builder(getApplicationContext());
            }

            dialog.setTitle("NETWORK CONNECTION")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finishAffinity();
                        }
                    })
                    .setCancelable(false)
                    .setMessage("Please check Internet connection.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(WeatherActivity.this, "You are no longer receiving location updates.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        displayLocation();
    }

    private void setUpViewPager(ViewPager viewPager){
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentOne());
        adapter.addFragment(new FragmentTwo());
        adapter.addFragment(new FragmentThree());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

    private void updateAdapter(){
        mViewPager.getAdapter().notifyDataSetChanged();
        mTabLayout.getTabAt(0).setIcon(R.drawable.water_drop);
        mTabLayout.getTabAt(1).setIcon(R.drawable.atm_pressure);
        mTabLayout.getTabAt(2).setIcon(R.drawable.wind_top);
    }

    /*
    * ---------------------------GetWeather class---------------------------
    */
    private class GetWeather extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            HelperClass http = new HelperClass();
            return http.getHTTPData(urlString);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new Gson();
            Type type = new TypeToken<OpenWeatherMap>() {
            }.getType();
            OpenWeatherMap openWeatherMap = gson.fromJson(s, type);

            storeName(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            storeHumidity(openWeatherMap.getMain().getHumidity());
            storePressure((float)openWeatherMap.getMain().getPressure());
            storeWind((float)openWeatherMap.getWind().getSpeed());

            updateAdapter();
        }
    }

    private void storeName(String mCity) {
        SharedPreferences mSharedPreferences = getSharedPreferences("CityName", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("city", mCity);
        mEditor.apply();
    }

    private void storeHumidity(int mHumidity) {
        SharedPreferences mSharedPreferences = getSharedPreferences("HumidityLevel", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt("humidity", mHumidity);
        mEditor.apply();
    }

    private void storePressure(float mPressure) {
        SharedPreferences mSharedPreferences = getSharedPreferences("PressureLevel", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putFloat("pressure", mPressure);
        mEditor.apply();
    }

    private void storeWind(float mPressure) {
        SharedPreferences mSharedPreferences = getSharedPreferences("WindLevel", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putFloat("wind", mPressure);
        mEditor.apply();
    }


}
