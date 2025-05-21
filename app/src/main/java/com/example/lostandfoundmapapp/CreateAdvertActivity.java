package com.example.lostandfoundmapapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lostandfoundmapapp.database.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class CreateAdvertActivity extends AppCompatActivity {

    EditText etName, etPhone, etDescription, etDate, etLocation;
    RadioGroup radioGroup;
    Button btnSave, btnCurrentLocation;
    DBHelper dbHelper;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 100;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyB-0u148TNi6woTqWlApLf0Kh5SQBQzdW8");
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        radioGroup = findViewById(R.id.radioGroup);
        btnSave = findViewById(R.id.btnSave);
        btnCurrentLocation = findViewById(R.id.btnCurrentLocation);

        dbHelper = new DBHelper(this);

        etLocation.setOnClickListener(v -> {
            if (!isOnline()) {
                Toast.makeText(this, "Internet connection required for location search", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        btnSave.setOnClickListener(v -> {
            String type = (radioGroup.getCheckedRadioButtonId() == R.id.radioLost) ? "Lost" : "Found";
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String location = etLocation.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^\\+?[0-9]{7,15}$")) {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Enter a valid date in format dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }


            if (location.equalsIgnoreCase("Select Location") || location.length() < 3) {
                Toast.makeText(this, "Please choose a valid location", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = dbHelper.insertData(type, name, phone, description, date, location);
            if (inserted) {
                Toast.makeText(this, "Advert saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidDate(@NonNull String date) {
        return date.matches("^\\d{2}/\\d{2}/\\d{4}$");
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            String latLng = location.getLatitude() + ", " + location.getLongitude();
                            etLocation.setText(latLng);
                        } else {
                            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            etLocation.setText(place.getAddress());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
