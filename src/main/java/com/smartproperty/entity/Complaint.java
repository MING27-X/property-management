package com.smartproperty.entity;

import java.util.Date;

/**
 * 投诉建议
 */
public class Complaint {

    private Integer id;
    private Integer ownerId;
    /** 投诉 / 建议 */
    private String type;
    private String title;
    private String content;
    /** 待处理 / 已回复 */
    private String status;
    private String replyContent;
    private Integer replyBy;
    private Date replyTime;
    private Date createTime;

    /** 关联展示字段 */
    private String ownerName;
    private String ownerPhone;
    private String roomNo;
    private String buildingName;
    private String replyByName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Integer getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(Integer replyBy) {
        this.replyBy = replyBy;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getReplyByName() {
        return replyByName;
    }

    public void setReplyByName(String replyByName) {
        this.replyByName = replyByName;
    }
}
