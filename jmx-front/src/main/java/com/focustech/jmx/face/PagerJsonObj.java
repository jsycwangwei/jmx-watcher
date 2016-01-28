package com.focustech.jmx.face;

import net.sf.json.JSONObject;

public class PagerJsonObj {

    private int totalPage;
    private int currentPage;

    public PagerJsonObj(int totalCount, int currentPage) {
        this.totalPage = totalCount;
        this.currentPage = currentPage;
    }

    public int getTotalCount() {
        return totalPage;
    }

    public void setTotalCount(int totalCount) {
        this.totalPage = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public JSONObject toJson() {
        JSONObject pagejson = new JSONObject();
        pagejson.put("totalPages", this.totalPage);
        pagejson.put("currentPage", this.currentPage);
        return pagejson;
    }

}
