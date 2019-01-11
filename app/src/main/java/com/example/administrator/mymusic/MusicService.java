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
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicService  extends Service {
    public static MediaPlayer mediaPlayer=null;
    public static int index=0;
  //  public static ArrayList<String> urllist;    //本来想直接获取音乐地址的，但加载过慢
                                                 //主线程操作数据超过5秒都还没有完成就会提示程序未响应,这叫 anr ，故弃用
    /**
     * 初始化播放器
     */
    private void initMediaplayer() {
        try {

            mediaPlayer.setDataSource(Main3Activity.musiclist.get(index).get("url").toString());
         //   mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeMusic(int i)
    {
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            index=i;
            if(index<0)
                index=Main3Activity.musiclist.size()-1;
            if(index>=Main3Activity.musiclist.size())
                index=0;
            String url = Main3Activity.musiclist.get(index).get("url").toString();
            mediaPlayer.setDataSource(url);
    //        mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Main3Activity.CurrentMusic(index);
            Log.v("hjz","xsuccess "+index);
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

                            if(mm.arg1<0)
                            {
                                mm.arg1=0;
                            }
                            if(mm.arg2<0)
                            {
                                mm.arg2=0;
                            }

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

       // 监听播放完成的，因启动服务时也会触发，故需判断
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(Main3Activity.isplay==true) {
                    changeMusic(index + 1);
                }
            }
        });
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
            case "prev":
                if (mediaPlayer !=null)
                {
                    changeMusic(index-1);
                }
                Log.v("hjz","-------------prev,to="+index);
                break;
            case "next":
                if (mediaPlayer !=null)
                {
                    changeMusic(index+1);
                }
                Log.v("hjz","-------------next,to="+index);
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
