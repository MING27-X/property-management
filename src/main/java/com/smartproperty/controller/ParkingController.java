package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Parking;
import com.smartproperty.service.OwnerService;
import com.smartproperty.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartproperty.entity.SysUser;
import javax.servlet.http.HttpSession;

/**
 * 停车位管理。
 */
@Controller
@RequestMapping("/parking")
public class ParkingController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String type,
                       Model model) {
        PageResult<Parking> page = parkingService.findPage(keyword, status, type, pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        model.addAttribute("owners", ownerService.findAll());
        model.addAttribute("totalCount", parkingService.countAll());
        model.addAttribute("freeCount", parkingService.countByStatus("空闲"));
        model.addAttribute("rentedCount", parkingService.countByStatus("已租用"));
        model.addAttribute("tempCount", parkingService.countByStatus("临时占用"));
        model.addAttribute("monthlyIncome", parkingService.sumRentedFee());
        return "parking/list";
    }

    @PostMapping("/save")
    public String save(Parking parking, RedirectAttributes ra) {
        String error = parkingService.save(parking);
        ra.addFlashAttribute(error != null ? "error" : "msg",
                error != null ? error : (parking.getId() == null ? "车位新增成功" : "车位信息已更新"));
        return "redirect:/parking/list";
    }

    @PostMapping("/rent")
    public String rent(@RequestParam Integer id,
                       @RequestParam Integer ownerId,
                       @RequestParam String plateNo,
                       RedirectAttributes ra) {
        String error = parkingService.rent(id, ownerId, plateNo);
        ra.addFlashAttribute(error != null ? "error" : "msg",
                error != null ? error : "车位租用办理成功");
        return "redirect:/parking/list";
    }

    @PostMapping("/release")
    public String release(@RequestParam Integer id, RedirectAttributes ra) {
        String error = parkingService.release(id);
        ra.addFlashAttribute(error != null ? "error" : "msg",
                error != null ? error : "车位退租办理成功");
        return "redirect:/parking/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除车位仅系统管理员可操作，物业员工可办理租用与退租");
            return "redirect:/parking/list";
        }
        String error = parkingService.delete(id);
        ra.addFlashAttribute(error != null ? "error" : "msg",
                error != null ? error : "车位已删除");
        return "redirect:/parking/list";
    }
}
