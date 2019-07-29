package com.zhao.dao.domain;

import java.util.Date;

public class School {
    private Integer id;

    private String objectid;

    private String name;

    private String address;

    private String region;

    private String edu_type;

    private String account_type;

    private String email;

    private String linkman;

    private String linkway;

    private Date start_date;

    private Date end_date;

    private Integer property;

    private Integer has_sub;

    private String sub_list;

    private Integer has_office;

    private String office;

    private String center;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid == null ? null : objectid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman == null ? null : linkman.trim();
    }

    public String getLinkway() {
        return linkway;
    }

    public void setLinkway(String linkway) {
        this.linkway = linkway == null ? null : linkway.trim();
    }

    public Integer getProperty() {
        return property;
    }

    public void setProperty(Integer property) {
        this.property = property;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office == null ? null : office.trim();
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getEdu_type() {
        return edu_type;
    }

    public void setEdu_type(String edu_type) {
        this.edu_type = edu_type;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Integer getHas_sub() {
        return has_sub;
    }

    public void setHas_sub(Integer has_sub) {
        this.has_sub = has_sub;
    }

    public String getSub_list() {
        return sub_list;
    }

    public void setSub_list(String sub_list) {
        this.sub_list = sub_list;
    }

    public Integer getHas_office() {
        return has_office;
    }

    public void setHas_office(Integer has_office) {
        this.has_office = has_office;
    }
}