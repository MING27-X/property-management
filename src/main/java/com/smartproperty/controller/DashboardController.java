package com.smartproperty.controller;

import com.smartproperty.common.ChartItem;
import com.smartproperty.common.Doughnut;
import com.smartproperty.common.PageResult;
import com.smartproperty.entity.FeeBill;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.Repair;
import com.smartproperty.entity.SysUser;
import com.smartproperty.service.BuildingService;
import com.smartproperty.service.ComplaintService;
import com.smartproperty.service.FeeBillService;
import com.smartproperty.service.NoticeService;
import com.smartproperty.service.OwnerService;
import com.smartproperty.service.ParkingService;
import com.smartproperty.service.RepairService;
import com.smartproperty.service.RoomService;
import com.smartproperty.service.SysUserService;
import com.smartproperty.service.VisitorService;
import com.smartproperty.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 首页概览：根据登录角色展示小区总体数据或业主个人数据。
 */
@Controller
public class DashboardController {

    /** 图表配色，与前端样式保持一致的 Element 风格色板 */
    private static final String C_BLUE = "#409eff";
    private static final String C_GREEN = "#67c23a";
    private static final String C_YELLOW = "#f6bd16";
    private static final String C_RED = "#f56c6c";
    private static final String C_PURPLE = "#8e7bff";

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FeeBillService feeBillService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private SysUserService userService;

    /**
     * 系统根路径：统一跳转到首页概览。
     * 登录成功后 LoginController 会重定向到 "/"，此处负责接住该请求，
     * 未登录时会先被 LoginInterceptor 拦截到登录页。
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        model.addAttribute("latestNotices", noticeService.findLatest(6));
        if (loginUser.isOwner()) {
            fillOwnerData(loginUser, model);
        } else {
            fillAdminData(model);
        }
        return "dashboard";
    }

    /** 物业员工与管理员：小区整体统计与图表 */
    private void fillAdminData(Model model) {
        String period = DateUtil.currentPeriod();
        long feeUnpaidCount = feeBillService.countByStatus("未缴");

        // ===== 顶部统计卡片 =====
        model.addAttribute("ownerCount", ownerService.countAll());
        model.addAttribute("buildingCount", buildingService.countAll());
        model.addAttribute("roomCount", roomService.countAll());
        model.addAttribute("repairPending", repairService.countByStatus("待处理"));
        model.addAttribute("feeUnpaidCount", feeUnpaidCount);
        model.addAttribute("visitorToday", visitorService.countToday());

        // ===== 图表数据 =====
        Map<String, Long> repairStat = repairService.countGroupByStatus();
        model.addAttribute("repairTotal", repairService.countAll());
        model.addAttribute("repairDoughnut", buildDoughnut(
                Arrays.asList("待处理", "处理中", "已完成"),
                Arrays.asList(repairStat.get("待处理"), repairStat.get("处理中"), repairStat.get("已完成")),
                Arrays.asList(C_YELLOW, C_BLUE, C_GREEN)));

        long feePaidCount = feeBillService.countByStatus("已缴");
        model.addAttribute("feePaidCount", feePaidCount);
        model.addAttribute("feeDoughnut", buildDoughnut(
                Arrays.asList("已缴费", "未缴费"),
                Arrays.asList(feePaidCount, feeUnpaidCount),
                Arrays.asList(C_GREEN, C_YELLOW)));

        Map<String, Integer> roleStat = userService.countByRole();
        model.addAttribute("adminCount", roleValue(roleStat, "ADMIN"));
        model.addAttribute("staffCount", roleValue(roleStat, "STAFF"));
        model.addAttribute("userOwnerCount", roleValue(roleStat, "OWNER"));
        model.addAttribute("roleDoughnut", buildDoughnut(
                Arrays.asList("系统管理员", "物业员工", "业主"),
                Arrays.asList((long) roleValue(roleStat, "ADMIN"),
                        (long) roleValue(roleStat, "STAFF"),
                        (long) roleValue(roleStat, "OWNER")),
                Arrays.asList(C_BLUE, C_GREEN, C_YELLOW)));

        long parkingFree = parkingService.countByStatus("空闲");
        long parkingRented = parkingService.countByStatus("已租用");
        long parkingTemp = parkingService.countByStatus("临时占用");
        model.addAttribute("parkingTotal", parkingService.countAll());
        model.addAttribute("parkingFree", parkingFree);
        model.addAttribute("parkingRented", parkingRented);
        model.addAttribute("parkingIncome", parkingService.sumRentedFee());
        long maxParking = Math.max(parkingFree, Math.max(parkingRented, parkingTemp));
        model.addAttribute("parkingBars", Arrays.asList(
                new ChartItem("空闲", String.valueOf(parkingFree), barHeight(parkingFree, maxParking), "green"),
                new ChartItem("已租用", String.valueOf(parkingRented), barHeight(parkingRented, maxParking), ""),
                new ChartItem("临时占用", String.valueOf(parkingTemp), barHeight(parkingTemp, maxParking), "orange")));

        // 近 6 个月收费趋势
        List<Map<String, Object>> rows = feeBillService.sumByPeriod();
        Collections.reverse(rows);
        List<ChartItem> monthBars = new ArrayList<>();
        BigDecimal maxPaid = BigDecimal.ZERO;
        for (Map<String, Object> row : rows) {
            BigDecimal paid = toDecimal(row.get("paid"));
            if (paid.compareTo(maxPaid) > 0) {
                maxPaid = paid;
            }
        }
        for (Map<String, Object> row : rows) {
            BigDecimal paid = toDecimal(row.get("paid"));
            double height = maxPaid.doubleValue() > 0
                    ? paid.doubleValue() / maxPaid.doubleValue() * 100 : 0;
            monthBars.add(new ChartItem(String.valueOf(row.get("period")),
                    paid.setScale(2, RoundingMode.HALF_UP).toString(), Math.max(height, 2)));
        }
        model.addAttribute("monthBars", monthBars);

        // ===== 其它统计 =====
        model.addAttribute("occupiedCount", roomService.countByStatus("已入住"));
        model.addAttribute("vacantCount", roomService.countByStatus("空置"));
        model.addAttribute("complaintPending", complaintService.countByStatus("待处理"));
        model.addAttribute("complaintReplied", complaintService.countByStatus("已回复"));
        model.addAttribute("noticeCount", noticeService.countAll());
        model.addAttribute("currentPeriod", period);
        model.addAttribute("monthReceivable", feeBillService.sumAmount(null, period, null));
        model.addAttribute("monthReceived", feeBillService.sumAmount("已缴", period, null));
        model.addAttribute("unpaidTotal", feeBillService.sumAmount("未缴", null, null));
        model.addAttribute("latestRepairs", repairService.findLatest(5));
    }

    /** 业主：个人房屋、账单、报修与公告 */
    private void fillOwnerData(SysUser loginUser, Model model) {
        Owner owner = ownerService.findByUserId(loginUser.getId());
        model.addAttribute("owner", owner);
        Integer ownerId = owner == null ? -1 : owner.getId();

        model.addAttribute("myRooms", owner == null ? null : roomService.findByOwnerId(ownerId));
        model.addAttribute("unpaidCount", feeBillService.countUnpaidByOwner(ownerId));
        model.addAttribute("unpaidAmount", feeBillService.sumAmount("未缴", null, ownerId));
        model.addAttribute("paidAmount", feeBillService.sumAmount("已缴", null, ownerId));
        model.addAttribute("totalAmount", feeBillService.sumAmount(null, null, ownerId));

        PageResult<FeeBill> bills = feeBillService.findPage(null, null, null, null, ownerId, 1, 5);
        model.addAttribute("myBills", bills.getList());

        PageResult<Repair> repairs = repairService.findPage(null, null, null, ownerId, 1, 5);
        model.addAttribute("myRepairs", repairs.getList());
        model.addAttribute("myRepairCount", repairs.getTotal());
    }

    // ==================== 工具方法 ====================

    private int roleValue(Map<String, Integer> roleStat, String role) {
        return roleStat.containsKey(role) ? roleStat.get(role) : 0;
    }

    private double barHeight(long value, long max) {
        return max > 0 ? value * 100.0 / max : 0;
    }

    /**
     * 根据各项数值生成环形图（conic-gradient 色带 + 图例）。
     */
    private Doughnut buildDoughnut(List<String> labels, List<Long> values, List<String> colors) {
        Doughnut doughnut = new Doughnut();
        List<ChartItem> items = new ArrayList<>();
        long total = 0;
        for (Long value : values) {
            total += value == null ? 0 : value;
        }
        doughnut.setTotal(total);
        if (total <= 0) {
            doughnut.setGradient("conic-gradient(#ebeef5 0% 100%)");
            doughnut.setItems(items);
            return doughnut;
        }
        StringBuilder gradient = new StringBuilder("conic-gradient(");
        double acc = 0;
        for (int i = 0; i < labels.size(); i++) {
            long value = values.get(i) == null ? 0 : values.get(i);
            double start = acc;
            acc += value * 100.0 / total;
            if (i > 0) {
                gradient.append(", ");
            }
            gradient.append(colors.get(i)).append(" ").append(percentText(start))
                    .append("% ").append(percentText(acc)).append("%");
            items.add(new ChartItem(labels.get(i), String.valueOf(value), value * 100.0 / total,
                    colors.get(i)));
        }
        gradient.append(")");
        doughnut.setGradient(gradient.toString());
        doughnut.setItems(items);
        return doughnut;
    }

    private String percentText(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    private BigDecimal toDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }
}
