package com.example.administrator.mymusic;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main3Activity extends AppCompatActivity {
    private static TextView hint,current,total;//声明提示信息的文本框
    public static SeekBar seekBar;
    private Button start1,stop,pause,begin1,end;
    private ImageButton start,begin,left,right;
    private boolean isplay=false,isrun=false;
    public static ListView list;
    public TextView music_url,music_name;
    public  static TextView music_current;

    public static String toTime(int time)
    {
        //time /=1000;
        int minute = time / 60;
        int hour = minute /60;
        int second = time %60;
        minute %=60;
        return String.format("%02d:%02d",minute,second);
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

    private void GetData()
    {
        ArrayList<HashMap<String, Object>> musiclist=getMusicFile(Main3Activity.this);
        if(musiclist.size()!=0)
        {
            final HashMap<String, Object> music = musiclist.get(0);
            music_current.setText(music.get("title").toString());
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    music_url= (TextView) view.findViewById(R.id.music_url);
                    music_name=(TextView) view.findViewById(R.id.music_name);
                    String musicurl = music_url.getText().toString();

                    MusicService.changeMusic(musicurl);
                    music_current.setText(music_name.getText().toString());
                    start.setImageResource(R.drawable.pause);
                    isplay=true;
                }
            });
        }
        ListAdapter adapter = new SimpleAdapter(Main3Activity.this,musiclist, R.layout.listitem, new String[] { "id","url","pic","title","artist","size","duration"}, new int[] {R.id.music_Id,R.id.music_url,R.id.pic_id, R.id.music_name,R.id.music_singer,R.id.music_size,R.id.music_time});
        list.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        music_current=(TextView)findViewById(R.id.music_current);

        list=(ListView)super.findViewById(R.id.music_list);
        GetData();


        //获取各功能按钮
        start=(ImageButton) findViewById(R.id.play);//播放
        left=(ImageButton) findViewById(R.id.left);//播放
        right=(ImageButton) findViewById(R.id.right);//播放
      //  start=(Button)findViewById(R.id.start);//播放
//        pause=(Button)findViewById(R.id.pause);//播放
//        stop=(Button)findViewById(R.id.stop);//播放
        begin=(ImageButton) findViewById(R.id.kill);//播放
      //  begin=(Button)findViewById(R.id.begin);//播放

//        end=(Button)findViewById(R.id.end);//播放

        start.setEnabled(false);
//        pause.setEnabled(false);
//        stop.setEnabled(false);

//        hint=(TextView)findViewById(R.id.textView);




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
                if(isrun==false) {
                    aa.putExtra("action", "begin");
                    startService(aa);
                    Toast toast =Toast.makeText(Main3Activity.this,"启动服务",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    start.setEnabled(true);
                    isrun=true;
                }
                else{
                    aa.putExtra("action", "pause");
                    start.setImageResource(R.drawable.play);
                    isplay=false;
                    stopService(aa);
                    Toast toast =Toast.makeText(Main3Activity.this,"停止服务",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    start.setEnabled(false);
                    isrun=false;
                }


//                pause.setEnabled(true);
//                stop.setEnabled(true);
            }
        });

/*        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                stopService(aa);

                start.setEnabled(false);
                pause.setEnabled(false);
                stop.setEnabled(false);
            }
        });*/

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(Main3Activity.this, MusicService.class);
                if(isplay==false){
                    aa.putExtra("action", "start");
                    start.setImageResource(R.drawable.pause);
                    isplay=true;
                }
                else{
                    aa.putExtra("action", "pause");
                    start.setImageResource(R.drawable.play);
                    isplay=false;
                }
                startService(aa);
            }
        });

       /* pause.setOnClickListener(new View.OnClickListener() {
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
        });*/
        toBegin();  //一开始触发 启动服务

    }
    private  void toBegin()
    {
        begin.performClick();
        Log.v("hjz","触发");
    }
    public ArrayList<HashMap<String, Object>> getMusicFile(Context context) {
        //ArrayList<Music>存放音乐
        ArrayList<HashMap<String, Object>> MusicFiles = new ArrayList<>();

        //查询媒体数据库
        ContentResolver resolver = context.getContentResolver();


        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        int i=0;
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
                    HashMap<String, Object> musicbox = new HashMap<String, Object>();
                    musicbox.put("id",id);
                    musicbox.put("title",title);
                    musicbox.put("artist",artist);
                    musicbox.put("url",url);
                    musicbox.put("album",album);
                    musicbox.put("duration",toTime(duration/1000));
                    musicbox.put("size",new java.text.DecimalFormat("#.00").format((size/1024.0/1024))+" MB");

                    switch (i%5)
                    {
                        case 0: musicbox.put("pic",R.drawable.tx11);break;
                        case 1: musicbox.put("pic",R.drawable.tx22);break;
                        case 2: musicbox.put("pic",R.drawable.tx33);break;
                        case 3: musicbox.put("pic",R.drawable.tx44);break;
                        case 4: musicbox.put("pic",R.drawable.tx55);break;
                    }
                    MusicFiles.add(musicbox);
                    i++;

                }
                cursor.moveToNext();
            }
        }
        return MusicFiles;
    }
    private Bitmap loadCover(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] cover = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
      //  image.setImageBitmap(bitmap);
        return bitmap;
    }
}
