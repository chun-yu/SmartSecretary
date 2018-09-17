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
    private static final String CHECK = "_check";
    private static final String DATE = "date";
    private static final String TITLE = "title";
    private static final String NOTE = "note";
    private static final String SONG = "song_name";
    private static final String SONGPATH = "song_path";
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
                    TIME + " CHAR, " +
                    CHECK + " TINYINT(1),"+
                    DATE + " TEXT(10)," +
                    TITLE + " TEXT(10), " +
                    NOTE + " TEXT(150), " +
                    SONG + " TEXT(50), " +
                    SONGPATH + " TEXT(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        //final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //db.execSQL(DROP_TABLE);
        //onCreate(db);
    }

    public void insertInfo(SQLiteDatabase db, String time,int check, String date, String title, String note, String song, String songPath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME, time);
        contentValues.put(CHECK, check);
        contentValues.put(DATE, date);
        contentValues.put(TITLE, title);
        contentValues.put(NOTE, note);
        contentValues.put(SONG, song);
        contentValues.put(SONGPATH, songPath);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public void updateTimeInfo(SQLiteDatabase db, int rowId, String time, int check, String date ,String title, String note, String song, String songPath) {
        String id = rowId + "";
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME, time);
        contentValues.put(CHECK, check);
        contentValues.put(DATE, date);
        contentValues.put(TITLE, title);
        contentValues.put(NOTE, note);
        contentValues.put(SONG, song);
        contentValues.put(SONGPATH, songPath);
        db.update(TABLE_NAME, contentValues, _ID + "=?", new String[] {id});
        //db.execSQL("Update " + TABLE_NAME + " set " + TIME + "=" + time + " Where " + COUNTER + "=" + id);
    }

    public void remove_Time(SQLiteDatabase db, int rowId) {
        String id = rowId + "";
        db.delete(TABLE_NAME, _ID + "=" + id, null);
    }
    public boolean remove_Note(int rowId) { //刪除指定的資料
        return getWritableDatabase().delete(TABLE_NAME, _ID + "=" + rowId, null) > 0;
    }
    public int getDBcount() {
        int result = 0;
        Cursor cursor = getWritableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public Cursor getInfo(SQLiteDatabase db) {
        return  db.query(TABLE_NAME, new String[] {_ID, TIME, CHECK, DATE , TITLE, NOTE, SONG, SONGPATH}, null, null, null, null, null);
    }
    public Cursor getInfoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

}
