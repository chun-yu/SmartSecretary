package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
    //private static final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String MEDIA_PATH = new String("/sdcard/");
    private ArrayList<String> songs = new ArrayList<String>();
    private ArrayList<String> songPath = new ArrayList<String>();
    private MediaPlayer mp = new MediaPlayer();
    Button btnBack, btnDetermine;
    ListView musicList;
    File SDCardPath;
    String musicPath = "";
    int choice = 0, index = 0;
    int songWakeUp = R.raw.wake_up;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        initView();
        updateSongList();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            index = bundle.getInt("index");
        choice = index;
        //Log.d("INDEX", index + "");

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mp.reset();
                if (position != 0) {
                    try {
                        //musicPath = SDCardPath + "/" + songs.get(position);
                        //mp.setDataSource(musicPath);
                        mp.setDataSource(songPath.get(position));
                        mp.prepare();
                    } catch (IOException e) {
                        Log.v(getString(R.string.app_name), e.getMessage());
                    }
                }
                else
                    mp = MediaPlayer.create(MusicList.this, songWakeUp);
                choice = position;
                mp.setLooping(true);
                mp.start();
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
                if (choice != index) {
                    Intent intent = new Intent(MusicList.this, AlarmSetting.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", index);
                    bundle.putString("song", songs.get(choice));
                    bundle.putString("songPath", musicPath);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    //finish();
                }
                finish();
            }
        });
    }

    public void updateSongList() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
            songs.clear();
            songPath.clear();
            songs.add("預設");
            songPath.add(null);

            String[] ext = {".mp3", ".awv", "m4a"};
            File file = Environment.getExternalStorageDirectory();
            Log.d("Path", file + "");
            searchSong(file ,ext);

            /*
            File home = new File(MEDIA_PATH);
            if (home.listFiles(new Mp3Filter) != null) {
                for (File file : home.listFiles(new Mp3Filter)) {
                    songs.add(file.getName());
                }
            }
            */
            /*
            SDCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            Log.d("SDCardPath", SDCardPath + "");
            File[] musics = SDCardPath.listFiles();
            if (musics != null) {
                Log.d("enter if ", "musics != null");
                for (File file : musics) {
                    Log.d("歌曲的位置", "→" + file);
                    songs.add(file.getName());
                }
            }
            for (int i = 0; i < songs.size(); i++)
                Log.d("Song", songs.get(i));
            */
            /*
            Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                songPath.add(path);
                songs.add(title);
            }
            */
            ArrayAdapter<String> songList = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, songs);
            musicList.setAdapter(songList);
            //musicList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            musicList.setItemChecked(index, true);
        }
    }

    public void searchSong(File file ,String[] ext){
        if(file != null) {
            if(file.isDirectory()) {
                File[] listFile = file.listFiles();
                if(listFile != null){
                    for(int i = 0; i < listFile.length; i++) {
                        searchSong(listFile[i], ext);
                    }
                }
            }
            else {
                String filePath = file.getAbsolutePath();
                String name = file.getName();
                for(int i = 0; i < ext.length; i++) {
                    if(filePath.endsWith(ext[i])) {
                        songPath.add(filePath + "/" + name);
                        songs.add(name);
                        break;
                    }
                }
            }
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
