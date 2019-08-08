package com.zhao.dao.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageInfo<T> {
    private int pageNum;
    private int pageSize;
    private int total;
    private List<T> list;
}
