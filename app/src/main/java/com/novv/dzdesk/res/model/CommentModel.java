package com.novv.dzdesk.res.model;

import android.content.Context;

import com.novv.dzdesk.live.PrefUtil;

/**
 * 评论实体类 Created by lijianglong on 2017/9/4.
 */

public class CommentModel extends ItemBean {

    //------->本条资源的属性
    //被评论资源的id
    private String resourceId;
    //被评论资源作者的id
    private String resourceUid;
    //被评论的时间


    //------->本条资源本条评论的属性
    //评论或者回复的内容
    private String commentContent;
    //该条评论的id
    private String commentId;
    //评论时间
    private String commentCreateTime;
    //评论点赞人数
    private String commentPraiseNum;
    //本条评论本人是否点赞
    private boolean isPraise;
    //本条评论的发送时间
    private String commentTime;


    //------->本条资源本条评论人的属性
    //评论用户ID
    private String commentUserId;
    //评论用户名字
    private String commentName;
    //评论用户的头像
    private String commentPicture;


    //------->本条资源本条回复的属性
    //回复内容
    private String replyContent;
    //回复时间
    private String replyCreateTime;
    //回复ID
    private String replyId;


    //------->本条资源本条回复人的属性
    //回复人名
    private String replyName;
    //回复人的头像
    private String replyPicture;
    //回复人id
    private String replyUserId;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommentModel{");
        sb.append("resourceId='").append(resourceId).append('\'');
        sb.append(", resourceUid='").append(resourceUid).append('\'');
        sb.append(", commentContent='").append(commentContent).append('\'');
        sb.append(", commentId='").append(commentId).append('\'');
        sb.append(", commentCreateTime='").append(commentCreateTime).append('\'');
        sb.append(", commentPraiseNum='").append(commentPraiseNum).append('\'');
        sb.append(", isPraise=").append(isPraise);
        sb.append(", commentTime='").append(commentTime).append('\'');
        sb.append(", commentUserId='").append(commentUserId).append('\'');
        sb.append(", commentName='").append(commentName).append('\'');
        sb.append(", commentPicture='").append(commentPicture).append('\'');
        sb.append(", replyContent='").append(replyContent).append('\'');
        sb.append(", replyCreateTime='").append(replyCreateTime).append('\'');
        sb.append(", replyId='").append(replyId).append('\'');
        sb.append(", replyName='").append(replyName).append('\'');
        sb.append(", replyPicture='").append(replyPicture).append('\'');
        sb.append(", replyUserId='").append(replyUserId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getCommentPraiseNum() {
        return commentPraiseNum;
    }

    public void setCommentPraiseNum(String commentPraiseNum) {
        this.commentPraiseNum = commentPraiseNum;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceUid() {
        return resourceUid;
    }

    public void setResourceUid(String resourceUid) {
        this.resourceUid = resourceUid;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentCreateTime() {
        return commentCreateTime;
    }

    public void setCommentCreateTime(String commentCreateTime) {
        this.commentCreateTime = commentCreateTime;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getCommentPicture() {
        return commentPicture;
    }

    public void setCommentPicture(String commentPicture) {
        this.commentPicture = commentPicture;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyCreateTime() {
        return replyCreateTime;
    }

    public void setReplyCreateTime(String replyCreateTime) {
        this.replyCreateTime = replyCreateTime;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getReplyPicture() {
        return replyPicture;
    }

    public void setReplyPicture(String replyPicture) {
        this.replyPicture = replyPicture;
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    public boolean isPraise(Context context) {
        PrefUtil.getBoolean(context, getCommentId(), false);
        return false;
    }

    public void setPraise(Context context,boolean praise) {
        PrefUtil.putBoolean(context, getCommentId(), praise);
        isPraise = praise;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}