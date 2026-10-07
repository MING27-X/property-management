package com.smartproperty.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 环形图数据：由服务端计算 conic-gradient 色带与图例，前端无需引入图表库。
 */
public class Doughnut {

    /** 环形图背景（CSS conic-gradient 字符串） */
    private String gradient;
    /** 图例项 */
    private List<ChartItem> items = new ArrayList<>();
    /** 合计值 */
    private long total;

    public String getGradient() {
        return gradient;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }

    public List<ChartItem> getItems() {
        return items;
    }

    public void setItems(List<ChartItem> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
