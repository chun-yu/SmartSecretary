package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/*
class Mp3Filter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3"));
    }
}
*/

public class MusicList extends Activity {
    private static final String MEDIA_PATH =Environment.getExternalStorageDirectory().getPath();
    private ArrayList<String> songs = new ArrayList<String>();
    private MediaPlayer mp = new MediaPlayer();
    Button btnBack, btnDetermine;
    ListView musicList;
    File SDCardPath;
    String musicPath = "";
    int choice = -1, index = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        initView();
        updateSongList();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            index = bundle.getInt("index");
        Log.e("INDEX", index + "");

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    mp.reset();
                    musicPath = SDCardPath + "/" + songs.get(position);
                    choice = position;
                    mp.setDataSource(musicPath);
                    mp.prepare();
                    mp.start();
                } catch(IOException e) {
                    Log.v(getString(R.string.app_name), e.getMessage());
                }

            }
        });

        btnBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDetermine.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (choice != -1) {
                    Intent intent = new Intent(MusicList.this, AlarmSetting.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", index);
                    bundle.putString("song", songs.get(choice));
                    bundle.putString("songPath", musicPath);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                    finish();
            }
        });
    }

    public void updateSongList() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED) ) {
            /*
            SDCardPath = Environment.getExternalStorageDirectory();
            SDCardPath = new File(MEDIA_PATH);
            Log.d("PATH", "→" + MEDIA_PATH);
            if (SDCardPath.listFiles( new Mp3Filter()).length > 0) {
                for (File file : SDCardPath.listFiles( new Mp3Filter())) {
                    Log.d("歌曲的位置", "→" + file);
                    songs.add(file.getName());
                }
                ArrayAdapter<String> songList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
                musicList.setAdapter(songList);
            }
            */
            Log.e("外部記憶體存在", Environment.isExternalStorageEmulated() +  "");

            SDCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            Log.e("SDCardPath", SDCardPath + "");
            File[] musics = SDCardPath.listFiles();
            if (musics != null) {
                for (File file : musics) {
                    Log.d("歌曲的位置", "→" + file);
                    songs.add(file.getName());
                }
            }
            ArrayAdapter<String> songList = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, songs);
            //musicList.setItemChecked(0, true);
            musicList.setAdapter(songList);
        }
    }

    void initView() {
        musicList = (ListView) findViewById(R.id.musicList);
        btnBack = (Button) findViewById(R.id.btnMusicBack);
        btnDetermine = (Button) findViewById(R.id.btnMusicDetermine);
    }


    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //mp.pause();
        if (mp != null)
        {
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
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