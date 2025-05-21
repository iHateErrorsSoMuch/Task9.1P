package com.example.lostandfoundmapapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "LostFound.db";
    public static final String TABLE_NAME = "Items";
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_DESC = "description";
    public static final String COL_DATE = "date";
    public static final String COL_LOCATION = "location";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TYPE + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LOCATION + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String type, String name, String phone, String desc, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE, type);
        cv.put(COL_NAME, name);
        cv.put(COL_PHONE, phone);
        cv.put(COL_DESC, desc);
        cv.put(COL_DATE, date);
        cv.put(COL_LOCATION, location);
        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public int deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }
}
