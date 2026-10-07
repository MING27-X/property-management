package com.smartproperty.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用分页结果对象。
 *
 * @param <T> 列表元素类型
 */
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> list = new ArrayList<>();
    /** 总记录数 */
    private long total;
    /** 当前页码，从 1 开始 */
    private int pageNum = 1;
    /** 每页条数 */
    private int pageSize = 10;

    public PageResult() {
    }

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list == null ? new ArrayList<T>() : list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /** 总页数 */
    public int getPages() {
        if (pageSize <= 0) {
            return 1;
        }
        int pages = (int) ((total + pageSize - 1) / pageSize);
        return pages == 0 ? 1 : pages;
    }

    /** SQL 查询起始行 */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public boolean isHasPrev() {
        return pageNum > 1;
    }

    public boolean isHasNext() {
        return pageNum < getPages();
    }

    /** 页码列表，用于页面渲染分页按钮 */
    public List<Integer> getPageNumbers() {
        List<Integer> numbers = new ArrayList<>();
        int total = getPages();
        int start = Math.max(1, pageNum - 2);
        int end = Math.min(total, start + 4);
        start = Math.max(1, end - 4);
        for (int i = start; i <= end; i++) {
            numbers.add(i);
        }
        return numbers;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        // 页码超出总页数时自动回到最后一页，避免查询到空白页
        if (pageSize > 0 && pageNum > getPages()) {
            this.pageNum = getPages();
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
