package com.smartproperty.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 业主档案
 */
public class Owner {

    private Integer id;
    private Integer userId;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    private Integer roomId;
    private Integer familyCount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date moveInDate;
    private String remark;
    private Date createTime;

    /** 关联展示字段 */
    private String roomNo;
    private String buildingName;
    private String buildingNo;
    private String username;

    /** 下拉框显示：张三（A1 1-101） */
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(name == null ? "" : name);
        if (roomNo != null) {
            sb.append("（").append(buildingNo == null ? "" : buildingNo).append(" ").append(roomNo).append("）");
        }
        return sb.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getFamilyCount() {
        return familyCount;
    }

    public void setFamilyCount(Integer familyCount) {
        this.familyCount = familyCount;
    }

    public Date getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(Date moveInDate) {
        this.moveInDate = moveInDate;
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

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
