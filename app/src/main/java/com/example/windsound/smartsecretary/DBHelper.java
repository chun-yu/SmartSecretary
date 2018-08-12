package com.example.windsound.smartsecretary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "my.db";
    private static final String TABLE_NAME = "alarm";
    private static final String TIME = "alarm_time";
    private static final String COUNTER = "alarm_number";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COUNTER + " INTEGER NOT NULL, " +
                    TIME + " CHAR NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        //final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //db.execSQL(DROP_TABLE);
        //onCreate(db);
    }

    public void insertInfo(SQLiteDatabase db, int counter, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COUNTER, counter);
        contentValues.put(TIME, time);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public void updateInfo(SQLiteDatabase db, int rowId, String time) {
        String id = rowId + "";
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME, time);
        db.update(TABLE_NAME, contentValues, COUNTER + "=?", new String[] {id});
        //db.execSQL("Update " + TABLE_NAME + " set " + TIME + "=" + time + " Where " + COUNTER + "=" + id);
    }

    public void removeInfo(SQLiteDatabase db, int rowId) {
        String id = rowId + "";
        db.delete(TABLE_NAME, COUNTER + "=" + id, null);
        db.execSQL("Update " + TABLE_NAME + " set " + COUNTER + "=" + COUNTER + "-1" + " Where " + COUNTER + ">" + id);
    }

    public Cursor getInfo(SQLiteDatabase db) {
        return  db.query(TABLE_NAME, new String[] {_ID, COUNTER, TIME}, null, null, null, null, null);
    }
}
