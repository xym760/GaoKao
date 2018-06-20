package com.nxist.gaokao.Module;

/**
 * Created by xym760 on 2018/3/31.
 */

public class MajorItem {
    private String majorName;
    private String majorCode;
    public MajorItem(String majorName,String majorCode) {
        this.majorName=majorName;
        this.majorCode=majorCode;
    }
    public String getMajorName() {
        return majorName;
    }
    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
    public String getMajorCode() {
        return majorCode;
    }
    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }
}
