package com.example.administrator.mymusic;

/**
 * Created by Administrator on 2019/1/3.
 */
public class Music {
    private int id;
    private String name;
    private String singerName;
    private String url;
    private String album;
    private int totalTime;
    private long size;

    public Music(int id, String name, String singerName,
                 String url, String album, int totalTime, long size) {
        this.id = id;
        this.name = name;
        this.singerName = singerName;
        this.url = url;
        this.album = album;
        this.totalTime = totalTime;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}

