package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Building;
import com.smartproperty.mapper.BuildingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 楼栋管理业务。
 */
@Service
public class BuildingService {

    @Autowired
    private BuildingMapper buildingMapper;

    public PageResult<Building> findPage(String keyword, int pageNum, int pageSize) {
        PageResult<Building> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(buildingMapper.countByCondition(keyword));
        page.setList(buildingMapper.findPage(keyword, page.getOffset(), pageSize));
        return page;
    }

    public List<Building> findAll() {
        return buildingMapper.findAll();
    }

    public Building findById(Integer id) {
        return buildingMapper.findById(id);
    }

    public long countAll() {
        return buildingMapper.countAll();
    }

    @Transactional
    public String save(Building building) {
        if (building.getBuildingNo() == null || building.getBuildingNo().trim().isEmpty()) {
            return "楼栋编号不能为空";
        }
        building.setBuildingNo(building.getBuildingNo().trim());
        if (buildingMapper.countByBuildingNo(building.getBuildingNo(), building.getId()) > 0) {
            return "楼栋编号【" + building.getBuildingNo() + "】已存在";
        }
        if (building.getUnitCount() == null) {
            building.setUnitCount(1);
        }
        if (building.getFloorCount() == null) {
            building.setFloorCount(1);
        }
        if (building.getId() == null) {
            buildingMapper.insert(building);
        } else {
            buildingMapper.update(building);
        }
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        if (buildingMapper.countRooms(id) > 0) {
            return "该楼栋下已存在房屋，不能删除";
        }
        buildingMapper.deleteById(id);
        return null;
    }
}
