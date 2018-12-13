package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AlarmReceiver extends BroadcastReceiver {
    private SoundPool sp;
    private int sourceid;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        int index = bundle.getInt("index");

        if (bundle.getString("msg").equals("time's_up")) {

            if(bundle.getString("title") != null && bundle.getString("title").length() != 0){
                notice(context,index,bundle.getString("title"),bundle.getLong("show_time"));
            }
            //sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
            //sourceid = sp.load(context, R.raw.wake_up, 1);
            //playSounds(1, context);
            //Toast.makeText(context, "time's up", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, AlarmWake.class);
            //i.putExtra("index", index);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else{
            String title = bundle.getString("title");
            long show_time = bundle.getLong("show_time");
            notice(context,index,title,show_time);
        }
    }

    public void playSounds(int repeatTime, Context context) {
        AudioManager am = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        // 獲取最大音量
        float audMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 獲取目前音量
        float audCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 左右聲道值範圍為 0.0 - 1.0
        float volRatio = audCurrentVolumn / audMaxVolumn;
        // 下面參數分別為播放音頻，左聲道,右聲道，設置優先級，重撥次數，速率(速率最低0.5，最高為2，1代表正常速度)
        sp.play(sourceid, volRatio, volRatio, 1, repeatTime, 1);
    }
    public void notice(Context context,int id,String title,long showtime){
        Intent intent = new Intent(context, memo.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"default")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(showtime)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("NOTICE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "NOTICE",
                    "SmartSecretary",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(id,builder.build());
    }
    public static void cancelNotice(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
