package com.example.administrator.mymusic;

/**
 * Created by Administrator on 2018/12/27.
 */
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class MusicService  extends Service {
    MediaPlayer mediaPlayer=null;

    /**
     * 初始化播放器
     */
    private void initMediaplayer() {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Download", "b.mp3");
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
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
}

