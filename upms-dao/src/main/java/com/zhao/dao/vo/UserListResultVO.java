package com.zhao.dao.vo;

import com.zhao.dao.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class UserListResultVO {

    public int code;

    public String msg;

    public String datetime;

    public UserListVO data;

    public static class UserListVO{
        private List<User> dataList;

        private PageInfo pageInfo;

        public List<User> getDataList() {
            return dataList;
        }

        public void setDataList(List<User> dataList) {
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
