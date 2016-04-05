package org.khmeracademy.Model;

/**
 * Created by Longdy on 12/20/2015.
 */
public class ListVideoItem {
    private String id;
    private String sectionTitle;
    private String videoTitle;
    private String duration;
    private int listVideoNum;
    private int bgNumColor;
    private String video_url;
    private int view_count;
    private int vote;

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getBgNumColor() {
        return bgNumColor;
    }

    public void setBgNumColor(int bgNumColor) {
        this.bgNumColor = bgNumColor;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getListVideoNum() {
        return listVideoNum;
    }

    public void setListVideoNum(int listVideoNum) {
        this.listVideoNum = listVideoNum;
    }
}
