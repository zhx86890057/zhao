package com.zhao.dao.vo;

import com.zhao.dao.domain.Depart;
import com.zhao.dao.domain.Parent;
import lombok.Data;

import java.util.List;

@Data
public class DepartListResultVO {
    public int code;

    public String msg;

    public String datetime;

    public DepartListVO data;

    public static class DepartListVO{
        private List<Depart> dataList;

        private PageInfo pageInfo;

        public List<Depart> getDataList() {
            return dataList;
        }

        public void setDataList(List<Depart> dataList) {
            this.dataList = dataList;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }
    }
}
