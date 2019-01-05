package com.example.administrator.mymusic;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {
    private static TextView hint,current,total;//声明提示信息的文本框
    public static SeekBar seekBar;
    private Button start,stop,pause,begin,end;

    public static String toTime(int time)
    {
        //time /=1000;
        int minute = time / 60;
        int hour = minute /60;
        int second = time %60;
        minute %=60;
        return String.format("%01d:%02d",minute,second);
    }

    public static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x01:
                    int tt1=msg.arg1;
                    int tt2=msg.arg2;
                    String str1=toTime(tt1);
                    String str2=toTime(tt2);
                    current.setText(str1);
                    total.setText(str2);

                    seekBar.setMax(msg.arg2);
                    seekBar.setProgress(msg.arg1);

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //获取各功能按钮
        start=(Button)findViewById(R.id.start);//播放
        pause=(Button)findViewById(R.id.pause);//播放
        stop=(Button)findViewById(R.id.stop);//播放
        begin=(Button)findViewById(R.id.begin);//播放
        end=(Button)findViewById(R.id.end);//播放

        start.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);

        hint=(TextView)findViewById(R.id.textView);

        current=(TextView)findViewById(R.id.current);
        total=(TextView)findViewById(R.id.total);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int ta=seekBar.getProgress();
                current.setText(toTime(ta));

                Intent aa=new Intent(Main3Activity.this,MusicService.class);
                aa.putExtra("action","change");
                aa.putExtra("currentP",ta);

                startService(aa);



                /* currentPosition=seekBar.getProgress();

                mediaPlayer.seekTo(currentPosition);
                current.setText(toTime(currentPosition));*/
            }
        });

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                aa.putExtra("action", "begin");
                startService(aa);

                start.setEnabled(true);
                pause.setEnabled(true);
                stop.setEnabled(true);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                stopService(aa);

                start.setEnabled(false);
                pause.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                aa.putExtra("action", "start");
                startService(aa);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                aa.putExtra("action", "pause");
                startService(aa);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                aa.putExtra("action", "stop");
                startService(aa);
            }
        });

        ArrayList<Music> aa=getMusicFile(Main3Activity.this);
        int yy=0;
    }

    public ArrayList<Music> getMusicFile(Context context) {
        //ArrayList<Music>存放音乐
        ArrayList<Music> MusicFiles = new ArrayList<>();

        //查询媒体数据库
        ContentResolver resolver = context.getContentResolver();


        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                //歌曲编号MediaStore.Audio.Media._ID
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                //歌曲标题
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

                //歌曲的专辑名MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

                //歌曲的歌手名MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                //歌曲文件的路径MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                //歌曲的总播放时长MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                //歌曲文件的大小MediaStore.Audio.Media.SIZE
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (size > 1024 * 800) {     //是否大于800K
                    if (title.equals("<unknown>") || title.equals("")) {
                        title = "未知";
                    }
                    if ("<unknown>".equals(artist) || "".equals(artist)) {
                        artist = "未知";
                    }

                    Music music = new Music(id, title, artist,
                            url, album, duration, size);
                    MusicFiles.add(music);
                }
                cursor.moveToNext();
            }
        }
        return MusicFiles;
    }
}
