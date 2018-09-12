package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class AlarmWake extends Activity {

    MediaPlayer mp;
    private DBHelper helper = null;
    int songWakeUp = R.raw.wake_up;
    int maxVolume = 100;
    Cursor cursor;
    PowerManager.WakeLock wl;
    final AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_wake);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        Button btnCloseAlarm = (Button) findViewById(R.id.btnCloseAlarm);
        //Bundle bundle = this.getIntent().getExtras();
        //final int index = bundle.getInt("index");

        final Calendar c = Calendar.getInstance();
        final int nowYear = c.get(Calendar.YEAR);
        final int nowMonth = c.get(Calendar.MONTH);
        final int nowDay = c.get(Calendar.DAY_OF_MONTH);
        final int nowHour = c.get(Calendar.HOUR_OF_DAY);
        final int nowMin = c.get(Calendar.MINUTE);

        final int index = findWhichAlarm(nowYear,nowMonth+1, nowDay, nowHour, nowMin);
        Log.d("index", index + "");

        am.setStreamVolume(AudioManager.STREAM_ALARM,
                am.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        //mp = new MediaPlayer();
        //mp = MediaPlayer.create(this, songWakeUp);
        //mp.setVolume(maxVolume, maxVolume);
        //mp.setLooping(true);
        //mp.start();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");

        wl.acquire();

        KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();

        btnCloseAlarm.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mp.stop();
                mp.reset();
                //helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), Alarm.alarmTimeList.get(index), 0, Alarm.alarmDateList.get(index), null, null);
                //android.os.Process.killProcess(android.os.Process.myPid());
                //finish();
                Intent intent = new Intent(AlarmWake.this, Alarm.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
    }

    int findWhichAlarm(int nowYear, int nowMonth, int nowDay, int nowHour, int nowMin) {
        cursor = helper.getInfo(helper.getReadableDatabase());
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String time = cursor.getString(1);
                int hour = Integer.parseInt(time.split(":")[0]);
                int min = Integer.parseInt(time.split(":")[1]);
                String date = cursor.getString(3);
                int year = Integer.parseInt(date.split("/")[0]);
                int month = Integer.parseInt(date.split("/")[1]);
                int day = Integer.parseInt(date.split("/")[2]);
                if (nowYear == year && nowMonth == month && nowDay == day && nowHour == hour && nowMin == min)
                    return i;
                cursor.moveToNext();
            }
        }
        return -1;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mp != null)
        {
            mp.release();
            mp = null;
        }
        wl.release();
    }
}
