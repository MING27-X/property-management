package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Parking;
import com.smartproperty.mapper.ParkingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 停车位管理业务。
 */
@Service
public class ParkingService {

    @Autowired
    private ParkingMapper parkingMapper;

    public PageResult<Parking> findPage(String keyword, String status, String type,
                                        int pageNum, int pageSize) {
        PageResult<Parking> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(parkingMapper.countByCondition(keyword, status, type));
        page.setList(parkingMapper.findPage(keyword, status, type, page.getOffset(), pageSize));
        return page;
    }

    public Parking findById(Integer id) {
        return parkingMapper.findById(id);
    }

    public long countAll() {
        return parkingMapper.countAll();
    }

    public long countByStatus(String status) {
        return parkingMapper.countByStatus(status);
    }

    public BigDecimal sumRentedFee() {
        BigDecimal value = parkingMapper.sumRentedFee();
        return value == null ? BigDecimal.ZERO : value;
    }

    @Transactional
    public String save(Parking parking) {
        if (parking.getSpaceNo() == null || parking.getSpaceNo().trim().isEmpty()) {
            return "车位编号不能为空";
        }
        parking.setSpaceNo(parking.getSpaceNo().trim());
        if (parkingMapper.countBySpaceNo(parking.getSpaceNo(), parking.getId()) > 0) {
            return "车位编号【" + parking.getSpaceNo() + "】已存在";
        }
        if (parking.getMonthlyFee() == null) {
            parking.setMonthlyFee(BigDecimal.ZERO);
        }
        if (parking.getArea() == null || parking.getArea().isEmpty()) {
            parking.setArea("地下车库");
        }
        if (parking.getType() == null || parking.getType().isEmpty()) {
            parking.setType("固定");
        }
        if (parking.getStatus() == null || parking.getStatus().isEmpty()) {
            parking.setStatus("空闲");
        }
        if (parking.getId() == null) {
            parkingMapper.insert(parking);
        } else {
            // 编辑表单不含租用人信息，这里保留原有租用数据，避免编辑后租用人/车牌被清空
            Parking old = parkingMapper.findById(parking.getId());
            if (old != null && "已租用".equals(parking.getStatus())) {
                if (parking.getOwnerId() == null) {
                    parking.setOwnerId(old.getOwnerId());
                }
                if (parking.getPlateNo() == null) {
                    parking.setPlateNo(old.getPlateNo());
                }
                if (parking.getRentStart() == null) {
                    parking.setRentStart(old.getRentStart());
                }
                if (parking.getRentEnd() == null) {
                    parking.setRentEnd(old.getRentEnd());
                }
            }
            if (!"已租用".equals(parking.getStatus())) {
                parking.setOwnerId(null);
                parking.setPlateNo(null);
                parking.setRentStart(null);
                parking.setRentEnd(null);
            }
            parkingMapper.update(parking);
        }
        return null;
    }

    /** 办理车位租用 */
    @Transactional
    public String rent(Integer id, Integer ownerId, String plateNo) {
        Parking parking = parkingMapper.findById(id);
        if (parking == null) {
            return "车位不存在";
        }
        if ("已租用".equals(parking.getStatus())) {
            return "该车位已被租用";
        }
        if (ownerId == null) {
            return "请选择租用业主";
        }
        if (plateNo == null || plateNo.trim().isEmpty()) {
            return "请填写车牌号";
        }
        parkingMapper.rent(id, ownerId, plateNo.trim().toUpperCase());
        return null;
    }

    /** 办理退租 */
    @Transactional
    public String release(Integer id) {
        Parking parking = parkingMapper.findById(id);
        if (parking == null) {
            return "车位不存在";
        }
        if (!"已租用".equals(parking.getStatus())) {
            return "该车位当前未处于租用状态";
        }
        parkingMapper.release(id);
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        Parking parking = parkingMapper.findById(id);
        if (parking == null) {
            return "车位不存在";
        }
        if ("已租用".equals(parking.getStatus())) {
            return "车位已租用，请先办理退租";
        }
        parkingMapper.deleteById(id);
        return null;
    }
}
