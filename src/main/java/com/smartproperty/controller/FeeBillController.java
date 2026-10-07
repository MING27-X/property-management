package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.FeeBill;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.SysUser;
import com.smartproperty.service.FeeBillService;
import com.smartproperty.service.OwnerService;
import com.smartproperty.service.RoomService;
import com.smartproperty.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * 收费管理。物业员工/管理员可维护全部账单，业主只能查看并缴纳自己的账单。
 */
@Controller
@RequestMapping("/fee")
public class FeeBillController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private FeeBillService feeBillService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String feeType,
                       @RequestParam(required = false) String period,
                       HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        Integer ownerId = null;
        Owner owner = null;
        if (loginUser.isOwner()) {
            owner = ownerService.findByUserId(loginUser.getId());
            if (owner != null) {
                ownerId = owner.getId();
            } else {
                ownerId = -1;   // 未绑定档案的业主账号，查不到任何账单
            }
            period = null;
            keyword = null;
        }

        PageResult<FeeBill> page = feeBillService.findPage(keyword, status, feeType, period,
                ownerId, pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("feeType", feeType);
        model.addAttribute("period", period);

        String currentPeriod = DateUtil.currentPeriod();
        model.addAttribute("currentPeriod", currentPeriod);
        if (loginUser.isOwner()) {
            model.addAttribute("totalAmount", feeBillService.sumAmount(null, null, ownerId));
            model.addAttribute("paidAmount", feeBillService.sumAmount("已缴", null, ownerId));
            model.addAttribute("unpaidAmount", feeBillService.sumAmount("未缴", null, ownerId));
            model.addAttribute("unpaidCount", feeBillService.countUnpaidByOwner(ownerId));
            model.addAttribute("owner", owner);
        } else {
            model.addAttribute("totalAmount", feeBillService.sumAmount(null, currentPeriod, null));
            model.addAttribute("paidAmount", feeBillService.sumAmount("已缴", currentPeriod, null));
            model.addAttribute("unpaidAmount", feeBillService.sumAmount("未缴", null, null));
            model.addAttribute("rooms", roomService.findOptions(true));
            model.addAttribute("periods", new String[]{
                    DateUtil.currentPeriod(), "2026-01", "2026-02", "2026-03", "2026-04"});
        }
        return "fee/list";
    }

    @PostMapping("/save")
    public String save(FeeBill bill, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权新增账单");
            return "redirect:/fee/list";
        }
        // 员工只能查看与核销缴费，账单的新增 / 修改由管理员负责
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "新增或修改费用账单仅系统管理员可操作");
            return "redirect:/fee/list";
        }
        String error = feeBillService.save(bill);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", bill.getId() == null ? "账单新增成功" : "账单信息已更新");
        }
        return "redirect:/fee/list";
    }

    @PostMapping("/pay")
    public String pay(@RequestParam Integer id,
                      @RequestParam(required = false) String payMethod,
                      HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        FeeBill bill = feeBillService.findById(id);
        if (bill == null) {
            ra.addFlashAttribute("error", "账单不存在");
            return "redirect:/fee/list";
        }
        // 业主只能缴纳本人的账单
        if (loginUser.isOwner()) {
            Owner owner = ownerService.findByUserId(loginUser.getId());
            if (owner == null || !owner.getId().equals(bill.getOwnerId())) {
                ra.addFlashAttribute("error", "只能缴纳本人的费用账单");
                return "redirect:/fee/list";
            }
        }
        String error = feeBillService.pay(id, payMethod);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "缴费成功，金额 " + bill.getAmount() + " 元");
        }
        return "redirect:/fee/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权删除账单");
            return "redirect:/fee/list";
        }
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除费用账单仅系统管理员可操作");
            return "redirect:/fee/list";
        }
        String error = feeBillService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "账单已删除");
        }
        return "redirect:/fee/list";
    }
}
