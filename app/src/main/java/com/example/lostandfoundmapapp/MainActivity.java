package com.example.lostandfoundmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnCreateAdvert, btnViewItems, btnShowMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnShowMap = findViewById(R.id.btnShowMap);

        btnCreateAdvert.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(intent);
        });

        btnViewItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ItemsListActivity.class);
            startActivity(intent);
        });

        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }
}
