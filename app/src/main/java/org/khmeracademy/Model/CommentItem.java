package org.khmeracademy.Model;

/**
 * Created by Pirang on 1/30/2016.
 */
public class CommentItem {
    private String cmt_id;
    private String cmt_date;
    private String cmt_text;
    private String userImageUrl;
    private String userName;
    private String video_id;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCmt_id() {
        return cmt_id;
    }

    public void setCmt_id(String cmt_id) {
        this.cmt_id = cmt_id;
    }

    public String getCmt_date() {
        return cmt_date;
    }

    public void setCmt_date(String cmt_date) {
        this.cmt_date = cmt_date;
    }

    public String getCmt_text() {
        return cmt_text;
    }

    public void setCmt_text(String cmt_text) {
        this.cmt_text = cmt_text;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
}
