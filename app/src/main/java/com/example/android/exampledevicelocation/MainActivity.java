package com.example.android.exampledevicelocation;
//*****************************************************************************************************************
//    based on: https://medium.com/@droidbyme/android-turn-on-gps-programmatically-d585cf29c1ef
//    used EasyPermission library to fetch manifest permissions for location (see implementation is build.gradle)
//    the github link for medium article code:
//    https://github.com/droidbyme/Location/blob/master/app/src/main/java/com/coders/location/MainActivity.java
//*****************************************************************************************************************

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.List;
import java.util.Locale;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private double wayLatitude = 0.0;
    private double wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    TextView cityTv;
    TextView countryTv;
    TextView latitudeTv;
    TextView longitudeTv;
    TextView addressTv;
    TextView adminTv;
    TextView codeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityTv = findViewById(R.id.place);
        countryTv = findViewById(R.id.country);
        latitudeTv = findViewById(R.id.latitude);
        longitudeTv = findViewById(R.id.longitude);
        addressTv = findViewById(R.id.address);
        adminTv = findViewById(R.id.admin);
        codeTv = findViewById(R.id.code);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // GPS is turn on
            isGPS = isGPSEnable;
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        try {
                            Geocoder geo = new Geocoder(getApplicationContext(), Locale.US);
                            List<Address> addresses = geo.getFromLocation
                                    (wayLatitude, wayLongitude, 1);
                            if (addresses.isEmpty()) {
                                cityTv.setText("Waiting for Location");
                            } else {
                                String city = addresses.get(0).getLocality();
                                String country = addresses.get(0).getCountryName();
                                String address = addresses.get(0).getAddressLine(0);
                                String adminArea = addresses.get(0).getAdminArea();
                                String countryCode = addresses.get(0).getCountryCode();
                                cityTv.setText(city);
                                countryTv.setText(country);
                                latitudeTv.setText(String.valueOf(wayLatitude));
                                longitudeTv.setText(String.valueOf(wayLongitude));
                                addressTv.setText(address);
                                adminTv.setText(adminArea);
                                codeTv.setText(countryCode);
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // getFromLocation() may sometimes fail
                        }
                    }
                    if (fusedLocationClient != null) {
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };

        if (!isGPS) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            return;
        }
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (!EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                && !EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            EasyPermissions.requestPermissions(this, "Please grant the location permission",
                    REQUEST_LOCATION_PERMISSION, perms);
        } else {
            //  important!!! - when you switch the GPS on and off - stops getting location data unless you
            // add the following line - i.e - request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            fusedLocationClient.getLastLocation().
                    addOnSuccessListener(MainActivity.this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            try {
                                Geocoder geo = new Geocoder(getApplicationContext(), Locale.US);
                                List<Address> addresses = geo.getFromLocation
                                        (wayLatitude, wayLongitude, 1);
                                if (addresses.isEmpty()) {
                                    cityTv.setText("Waiting for Location");
                                } else {
                                    String city = addresses.get(0).getLocality();
                                    String country = addresses.get(0).getCountryName();
                                    String address = addresses.get(0).getAddressLine(0);
                                    String adminArea = addresses.get(0).getAdminArea();
                                    String countryCode = addresses.get(0).getCountryCode();
                                    cityTv.setText(city);
                                    countryTv.setText(country);
                                    latitudeTv.setText(String.valueOf(wayLatitude));
                                    longitudeTv.setText(String.valueOf(wayLongitude));
                                    addressTv.setText(address);
                                    adminTv.setText(adminArea);
                                    codeTv.setText(countryCode);
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // getFromLocation() may sometimes fail
                            }
                        } else {
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        getLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                getLocation();
            }
        }
    }
}