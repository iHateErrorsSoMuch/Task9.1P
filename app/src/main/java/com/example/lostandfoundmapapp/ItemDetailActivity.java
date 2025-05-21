package com.example.lostandfoundmapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfoundmapapp.database.DBHelper;

public class ItemDetailActivity extends AppCompatActivity {

    TextView tvDescription, tvDate, tvLocation;
    Button btnRemove;
    DBHelper dbHelper;
    int itemId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        btnRemove = findViewById(R.id.btnRemove);
        dbHelper = new DBHelper(this);

        itemId = getIntent().getIntExtra("itemId", -1);

        if (itemId != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(itemId)});
            if (cursor.moveToFirst()) {
                String type = cursor.getString(1);
                String name = cursor.getString(2);
                String date = cursor.getString(5);
                String location = cursor.getString(6);

                tvDescription.setText(type + " " + name);
                tvDate.setText("On " + date);
                tvLocation.setText("At " + location);
            }
            cursor.close();
        }

        btnRemove.setOnClickListener(v -> {
            int deleted = dbHelper.deleteItem(itemId);
            if (deleted > 0) {
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
