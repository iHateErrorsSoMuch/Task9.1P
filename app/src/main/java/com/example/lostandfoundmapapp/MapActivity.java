package com.example.lostandfoundmapapp;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfoundmapapp.database.DBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;
    private Geocoder geocoder;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new DBHelper(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Cursor cursor = dbHelper.getAllData();
        LatLng lastLatLng = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String type = cursor.getString(1);        // Lost / Found
                String description = cursor.getString(4); // Description
                String location = cursor.getString(6);    // latitude, longitude or text address

                Log.d(TAG, "Location: " + location);
                LatLng latLng = null;

                if (location.contains(",")) {
                    try {
                        String[] parts = location.split(",");
                        double lat = Double.parseDouble(parts[0].trim());
                        double lng = Double.parseDouble(parts[1].trim());
                        latLng = new LatLng(lat, lng);
                    } catch (Exception e) {
                        Log.e(TAG, "Invalid coordinates: " + location, e);
                    }
                }
                if (latLng == null) {
                    try {
                        List<Address> results = geocoder.getFromLocationName(location, 1);
                        if (results != null && !results.isEmpty()) {
                            Address addr = results.get(0);
                            latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
                        } else {
                            Log.w(TAG, "Geocoding returned no results for: " + location);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Geocoding failed for: " + location, e);
                    }
                }
                if (latLng != null) {
                    lastLatLng = latLng;
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(type)
                            .snippet(description));
                }

            } while (cursor.moveToNext());
        }

        // Zoom to the last marker or default to Melbourne
        if (lastLatLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12f));
        } else {
            LatLng melbourne = new LatLng(-37.8136, 144.9631);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 11f));
        }
    }
}
