package com.smartproperty.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋
 */
public class Room {

    private Integer id;
    private Integer buildingId;
    private String roomNo;
    private Integer floor;
    private BigDecimal area;
    /** 住宅 / 商铺 / 车库 */
    private String roomType;
    /** 已入住 / 空置 / 装修中 */
    private String status;
    private Integer ownerId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /** 关联展示字段 */
    private String buildingNo;
    private String buildingName;
    private String ownerName;
    private String ownerPhone;

    /** 下拉框显示文本 */
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildingNo == null ? "" : buildingNo).append(" ").append(roomNo == null ? "" : roomNo);
        sb.append("（").append(area == null ? "0" : area.stripTrailingZeros().toPlainString()).append("㎡）");
        if (ownerName != null && !ownerName.isEmpty()) {
            sb.append(" - ").append(ownerName);
        }
        return sb.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
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
}
