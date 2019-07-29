package com.zhao.dao.vo;

import lombok.Data;

@Data
public class PageInfo {
    private Integer page;

    private Integer size;

    private Integer total;
}
