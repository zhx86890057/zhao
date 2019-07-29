package com.zhao.dao.vo;

import com.zhao.dao.domain.School;
import lombok.Data;

@Data
public class SchoolInfoResultVO {
    public int code;

    public String msg;

    public String datetime;

    public School data;
}
