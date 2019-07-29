package com.zhao.dao.vo;

import lombok.Data;

import java.util.List;

@Data
public class SchoolListVO {
    public List<DataList> dataList;

    public PageInfo pageInfo;

    public static class DataList{
        private String objectid;

        private String name;

        public String getObjectid() {
            return objectid;
        }

        public void setObjectid(String objectid) {
            this.objectid = objectid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
