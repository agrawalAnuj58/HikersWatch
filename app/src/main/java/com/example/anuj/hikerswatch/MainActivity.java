package com.example.anuj.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastLocation != null)
                    updateLocation(lastLocation);
            }
        }
    }

    public void updateLocation(Location location){

        TextView latitudeView = findViewById(R.id.latitudeView);
        TextView longitudeView = findViewById(R.id.longitudeView);
        TextView accuracyView = findViewById(R.id.accuracyView);
        TextView altitudeView = findViewById(R.id.altitudeView);
        TextView address1View = findViewById(R.id.address1View);

        String lat = "Latitude : ";
        String lon = "Longitude : ";
        String accu = "Accuracy : ";
        String alti = "Altitude : ";


        lat = lat.concat(String.valueOf(location.getLatitude()));
        lon = lon.concat(String.valueOf(location.getLongitude()));

        if (location.hasAccuracy())
            accu = accu.concat(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude())
            alti = alti.concat(String.valueOf(location.getAltitude()));

        latitudeView.setText(lat);
        longitudeView.setText(lon);
        accuracyView.setText(accu);
        altitudeView.setText(alti);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Address add = addresses.get(0);
                int max = add.getMaxAddressLineIndex();
                String currentAddress = "";

                if (max != -1) {
                    for (int i = 0; i <= max; i++)
                        currentAddress += add.getAddressLine(i) + "\n";
                }

                address1View.setText(currentAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Log.i("ert",provider);
            }
        };

        if (Build.VERSION.SDK_INT < 23)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            else{

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null){
                    updateLocation(lastLocation);
                }
            }
        }
    }
}
