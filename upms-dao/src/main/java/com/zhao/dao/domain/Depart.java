package com.zhao.dao.domain;

public class Depart {
    private Integer id;

    private String departid;

    private String departname;

    private String departcode;

    private String level;

    private String parentid;

    private String wxdepartid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartid() {
        return departid;
    }

    public void setDepartid(String departid) {
        this.departid = departid == null ? null : departid.trim();
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname == null ? null : departname.trim();
    }

    public String getDepartcode() {
        return departcode;
    }

    public void setDepartcode(String departcode) {
        this.departcode = departcode == null ? null : departcode.trim();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level == null ? null : level.trim();
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid == null ? null : parentid.trim();
    }

    public String getWxdepartid() {
        return wxdepartid;
    }

    public void setWxdepartid(String wxdepartid) {
        this.wxdepartid = wxdepartid == null ? null : wxdepartid.trim();
    }
}