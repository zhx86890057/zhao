package com.zhao.dao.vo;

import com.zhao.dao.domain.Parent;
import lombok.Data;

import java.util.List;

@Data
public class ParentListResultVO {

    public int code;

    public String msg;

    public String datetime;

    public ParentListVO data;

    public static class ParentListVO{
        private List<Parent> dataList;

        private PageInfo pageInfo;

        public List<Parent> getDataList() {
            return dataList;
        }

        public void setDataList(List<Parent> dataList) {
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
