package com.example.windsound.smartsecretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    DBHelper helper = null;
    Cursor cursor;

    @Override
    public void onReceive(Context context, Intent intent) {
        helper = new DBHelper(context);

        cursor = helper.getInfo(helper.getReadableDatabase());
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(0);
                String time = cursor.getString(1);
                int hour = Integer.parseInt(time.split(":")[0]);
                int min = Integer.parseInt(time.split(":")[1]);
                int check = cursor.getInt(2);
                String date = cursor.getString(3);
                int year = Integer.parseInt(date.split("/")[0]);
                int month = Integer.parseInt(date.split("/")[1]);
                int day = Integer.parseInt(date.split("/")[2]);

                if (check == 1)
                    Alarm.setAlarm(context, year, month, day, hour, min, id);
                cursor.moveToNext();
            }
        }
    }
}
