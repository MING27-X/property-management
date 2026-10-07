package com.smartproperty.common;

/**
 * 首页图表元素：用于柱状图与进度条渲染。
 */
public class ChartItem {

    /** 显示名称，如 2026-03 */
    private String label;
    /** 显示数值，如 1669.10 或 85% */
    private String value;
    /** 高度 / 进度百分比，0-100 */
    private double percent;
    /** 附加样式，如 green / orange / red */
    private String cssClass;

    public ChartItem() {
    }

    public ChartItem(String label, String value, double percent) {
        this(label, value, percent, null);
    }

    public ChartItem(String label, String value, double percent, String cssClass) {
        this.label = label;
        this.value = value;
        this.percent = percent;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
