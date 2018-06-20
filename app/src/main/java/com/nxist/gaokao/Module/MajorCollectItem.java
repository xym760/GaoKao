package com.nxist.gaokao.Module;

public class MajorCollectItem {
    private String majorName;
    private String majorCode;
    private String collectDate;
    private Integer majorId;//院校Id
    public MajorCollectItem(String majorName,String majorCode) {
        this.majorName=majorName;
        this.majorCode=majorCode;
    }
    public MajorCollectItem(String majorName,String majorCode,String collectDate,Integer majorId) {
        this.majorName=majorName;
        this.majorCode=majorCode;
        this.collectDate=collectDate;
        this.majorId=majorId;
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
    public String getCollectDate() {
        return collectDate;
    }
    public void setCollectDate(String collectDate) {
        this.collectDate = collectDate;
    }
    public Integer getMajorId() {
        return majorId;
    }
    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
    }

}
