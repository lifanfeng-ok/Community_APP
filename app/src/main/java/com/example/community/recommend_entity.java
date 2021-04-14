package com.example.community;

import java.io.Serializable;

public class recommend_entity implements Serializable {
    private String name;
    private String iurl;
    private int notice_num;
    private int collect_num;
    private int comment_num;
    private int common_notice_num;
    private int common_video_num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIurl() {
        return iurl;
    }

    public void setIurl(String iurl) {
        this.iurl = iurl;
    }

    public int getNotice_num() {
        return notice_num;
    }

    public void setNotice_num(int notice_num) {
        this.notice_num = notice_num;
    }

    public int getCollect_num() {
        return collect_num;
    }

    public void setCollect_num(int collect_num) {
        this.collect_num = collect_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getCommon_notice_num() {
        return common_notice_num;
    }

    public void setCommon_notice_num(int common_notice_num) {
        this.common_notice_num = common_notice_num;
    }

    public int getCommon_video_num() {
        return common_video_num;
    }

    public void setCommon_video_num(int common_video_num) {
        this.common_video_num = common_video_num;
    }


}
