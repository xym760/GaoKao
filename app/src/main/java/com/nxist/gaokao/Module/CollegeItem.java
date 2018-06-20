package com.nxist.gaokao.Module;

/**
 * Created by 徐源茂 on 2018/3/29.
 */

public class CollegeItem {
    private String name;
    private String province;
    private String collectDate;
    private Integer collegeId;//院校Id
    public CollegeItem(String name,String province) {
        this.name=name;
        this.province=province;
    }
    public CollegeItem(String name,String province,String collectDate,Integer collegeId) {
        this.name=name;
        this.province=province;
        this.collectDate=collectDate;
        this.collegeId=collegeId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCollectDate() {
        return collectDate;
    }
    public void setCollectDate(String collectDate) {
        this.collectDate = collectDate;
    }
    public Integer getCollegeId() {
        return collegeId;
    }
    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }
}
