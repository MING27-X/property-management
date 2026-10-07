package com.smartproperty.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 访客登记
 */
public class Visitor {

    private Integer id;
    private String visitorName;
    private String phone;
    private Integer roomId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date visitTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date leaveTime;
    private String purpose;
    /** 待到访 / 已到访 / 已离开 */
    private String status;
    private Integer registerBy;
    private String remark;
    private Date createTime;

    /** 关联展示字段 */
    private String roomNo;
    private String buildingName;
    private String registerByName;
    private String ownerName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public Date getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRegisterBy() {
        return registerBy;
    }

    public void setRegisterBy(Integer registerBy) {
        this.registerBy = registerBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getRegisterByName() {
        return registerByName;
    }

    public void setRegisterByName(String registerByName) {
        this.registerByName = registerByName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
