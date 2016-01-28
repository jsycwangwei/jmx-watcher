package com.focustech.jmx.face;

public class EventResult {
    private boolean success = true;
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setFailureResult(String msg) {
        this.success = false;
        this.msg = msg;
    }

}
