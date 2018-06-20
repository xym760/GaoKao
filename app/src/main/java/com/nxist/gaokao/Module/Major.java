package com.nxist.gaokao.Module;

public class Major {
    private Integer majorId;
    private String majorName;
    private String majorCode;
    private String category;
    private String intro;
    public Major() {
        super();
    }
    public Integer getMajorId() {
        return majorId;
    }
    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
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
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getIntro() {
        return intro;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
}
