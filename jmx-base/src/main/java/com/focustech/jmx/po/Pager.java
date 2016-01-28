package com.focustech.jmx.po;

import java.util.List;

public class Pager<T> {
    private int pageSize = 20;// 每页显示的记录数,默认值是20
    private List<T> items;// 查询的记录结果集合
    private int totalCount;// 总记录数
    private int currentPage;// 当前页数
    private int aTotal = 3;// 一个分页条中超链接的个数,默认为10

    public int getaTotal() {
        return aTotal;
    }

    public void setaTotal(int aTotal) {
        this.aTotal = aTotal;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 获取分页的总页数
     * 
     * @return
     */
    public int getTotalPage() {
        int totalPage;
        if (totalCount % this.pageSize == 0) {
            totalPage = totalCount / this.pageSize;
        }
        else {
            totalPage = totalCount / this.pageSize + 1;
        }
        return totalPage;
    }

    /**
     * 获取查询开始的行号
     * 
     * @return
     */
    public int getStart() {
        return (currentPage - 1) * pageSize;
    }

    /**
     * 获取分页栏的第一个页码和最后一个页码
     * 
     * @return
     */
    public PageNum getPageNum() {
        PageNum pageNum = new PageNum();
        int totalPage = getTotalPage();// 总页数
        int padding = (int) Math.ceil(aTotal) / 2;// 中间的超链接距离边缘超链接的个数
        int start;
        int end;
        if (totalPage <= aTotal) {
            start = 1;
            end = totalPage;
        }
        else {
            if (currentPage <= padding) {
                start = 1;
                end = start + aTotal - 1;
            }
            else {
                start = currentPage - padding;
                if (currentPage + padding > totalPage) {
                    end = totalPage;
                }
                else {
                    end = currentPage + padding;
                }
            }
        }
        pageNum.setStart(start);
        pageNum.setEnd(end);
        return pageNum;
    }

    /**
     * 分页导航栏
     * 
     * @return
     */
    public String getPaginationBar(String href) {
        // 如果总记录数小于等于每页显示的记录则不需要显示分享栏
        if (totalCount <= pageSize)
            return null;
        StringBuilder sb = new StringBuilder();
        PageNum pageNum = getPageNum();
        int start = pageNum.getStart();
        int end = pageNum.getEnd();
        sb.append("<ul class=\"pagination\">");
        if (currentPage - 1 > 0) {
            sb.append(" <li><a href=\"" + href + "&page=1\">首页</a></li>").append("\n");
            sb.append(" <li><a href=\"" + href + "&page=" + (currentPage - 1) + "\">&laquo;" + "</a></li>");
        }
        for (int i = start; i <= end; i++) {
            sb.append(
                    " <li><a href=\"" + href + "&page=" + i + "\" " + (i == currentPage ? "style=\"color:red;\"" : "")
                            + ">" + i + "</a></li>").append("\n");
            if (i == end && currentPage != getTotalPage()) {
                sb.append(" <li><a href=\"" + href + "&page=" + (currentPage + 1) + "\">&raquo;</a></li>");
                sb.append(" <li><a href=\"" + href + "&page=" + getTotalPage() + "\">尾页</a><li>");
            }
        }
        sb.append("</ul>");
        return sb.toString();
    }

    /**
     * 用于保存一个分页栏开始的页码和结束的页码
     */
    class PageNum {
        int strat;
        int end;

        public void setStart(int start) {
            this.strat = start;
        }

        public int getStart() {
            return this.strat;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getEnd() {
            return this.end;
        }
    }
}
