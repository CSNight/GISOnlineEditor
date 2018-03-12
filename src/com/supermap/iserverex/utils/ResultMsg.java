package com.supermap.iserverex.utils;

public class ResultMsg {
    private String ns_type;
    private Integer status = 0;
    private String message = "暂无";
    private Object element;
    private int totalCount = 0;

    public void setNs_type(String ns_type) {
        this.ns_type = ns_type;
    }

    public void setTotalCount(int totalcount) {
        this.totalCount = totalcount;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNs_type() {
        return (ns_type);
    }

    public int getTotalCount() {
        return (totalCount);
    }

    public Integer getStatus() {
        return (status);
    }

    public String getMessage() {
        return (message);
    }

    public Object getElement() {
        return (element);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setElement(Object element) {
        this.element = element;
    }

}
