package com.nxist.gaokao.Module;


import java.util.List;

/**
 * Created by xym760 on 2018/4/2.
 */

public class TopicData {
    private int topicId;
    private int userId;
    private String userName;
    private String date;
    private String content;
    private String userIcon;//用户头像文件字符串
    private List<String> pictureList;//用户上传图片文件字符串列表
    private Boolean isHavePicture;
    private String stringPictureList;
    private Boolean isLike;
    private int likeNumber;//点赞数
    private int replyNumber;//回复数

    public TopicData(){
        super();
    }

    public TopicData(String userName, String date, String content, String userIcon, List<String> pictureList, Boolean isHavePicture){
        this.userName=userName;
        this.date=date;
        this.content=content;
        this.userIcon=userIcon;
        this.pictureList=pictureList;
        this.isHavePicture=isHavePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<String> pictureList) {
        this.pictureList = pictureList;
    }

    public Boolean getIsHavePicture() {
        return isHavePicture;
    }

    public void setIsHavePicture(Boolean isHavePicture) {
        this.isHavePicture = isHavePicture;
    }

    public String getStringPictureList() {
        return stringPictureList;
    }

    public void setStringPictureList(String stringPictureList) {
        this.stringPictureList = stringPictureList;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Boolean getIsLike() {
        return isLike;
    }

    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public int getReplyNumber() {
        return replyNumber;
    }

    public void setReplyNumber(int replyNumber) {
        this.replyNumber = replyNumber;
    }

}
