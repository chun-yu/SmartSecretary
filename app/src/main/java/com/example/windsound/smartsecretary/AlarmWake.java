package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlarmWake extends Activity {

    MediaPlayer mp;
    private DBHelper helper = null;
    int songWakeUp = R.raw.wake_up;
    int maxVolume = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_wake);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        Button btnCloseAlarm = (Button) findViewById(R.id.btnCloseAlarm);
        Bundle bundle = this.getIntent().getExtras();
        final int index = bundle.getInt("index");

        mp = new MediaPlayer();
        mp = MediaPlayer.create(this, songWakeUp);
        mp.setVolume(maxVolume, maxVolume);
        mp.setLooping(true);
        mp.start();

        btnCloseAlarm.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mp.stop();
                mp.reset();
                //helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), Alarm.alarmTimeList.get(index), 0, Alarm.alarmDateList.get(index), null, null);
                //android.os.Process.killProcess(android.os.Process.myPid());
                finish();
            }
        });
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
    }
}
