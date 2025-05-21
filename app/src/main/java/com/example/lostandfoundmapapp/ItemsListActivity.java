package com.example.lostandfoundmapapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfoundmapapp.database.DBHelper;

import java.util.ArrayList;

public class ItemsListActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;
    ArrayList<String> itemTitles;
    ArrayList<Integer> itemIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        listView = findViewById(R.id.listViewItems);
        dbHelper = new DBHelper(this);

        itemTitles = new ArrayList<>();
        itemIds = new ArrayList<>();

        Cursor cursor = dbHelper.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String type = cursor.getString(1);
                String desc = cursor.getString(4);

                itemIds.add(id);
                itemTitles.add(type + " - " + desc);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemTitles);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                int itemId = itemIds.get(position);
                Intent intent = new Intent(ItemsListActivity.this, ItemDetailActivity.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            });
        }
    }
}

