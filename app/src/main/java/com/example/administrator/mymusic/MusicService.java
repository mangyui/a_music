package com.example.administrator.mymusic;

/**
 * Created by Administrator on 2018/12/27.
 */
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicService  extends Service {
    public static MediaPlayer mediaPlayer=null;
    public static int index=0;
    /**
     * 初始化播放器
     */
    private void initMediaplayer() {
        mediaPlayer.reset();
        try {
            ArrayList<String> urllist=getMusic(MusicService.this);

            mediaPlayer.setDataSource(urllist.get(index));
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeMusic(String url)
    {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.v("hjz","xsuccess");
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("hjz","xerror");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer=new MediaPlayer();
        initMediaplayer();

        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        if(mediaPlayer!=null)
                        {
                            android.os.Message mm =new android.os.Message();
                            mm.what=0x01;
                            mm.arg1=Math.round(mediaPlayer.getCurrentPosition()/1000);
                            mm.arg2=Math.round(mediaPlayer.getDuration()/1000);

                     //       Main3Activity.mHandler.postDelayed(this, 1000);
                            Main3Activity.mHandler.sendMessage(mm);
                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){
                         e.printStackTrace();
                    }
                }

            }
        }.start();

        Log.v("hjz","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        int cc=intent.getIntExtra("currentP",0);
        switch (action)
        {
            case "begin":
                Log.v("hjz","-------------begin,startId="+startId);
                break;
            case "start":
                if (mediaPlayer == null)
                {
                    mediaPlayer=new MediaPlayer();
                    initMediaplayer();
                }
                mediaPlayer.start();
                Log.v("hjz","-------------start,startId="+startId);
                break;
            case "stop":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Log.v("hjz","-------------stop,startId="+startId);
                break;
            case "pause":
                if (mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
                Log.v("hjz","-------------pause,startId="+startId);
                break;
            case "change":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.seekTo(cc*1000);
                }
                Log.v("hjz","-------------change,cc="+cc);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer !=null)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Log.v("hjz","onDestroy");
    }

    public ArrayList<String> getMusic(Context context) {
        //ArrayList<Music>存放音乐
        ArrayList<String> MusicUrl = new ArrayList<>();

        //查询媒体数据库
        ContentResolver resolver = context.getContentResolver();


        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        //遍历媒体数据库
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                //歌曲文件的路径MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                MusicUrl.add(url);
                cursor.moveToNext();
            }
        }
        return MusicUrl;
    }
}
