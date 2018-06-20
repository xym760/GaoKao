package com.nxist.gaokao.Module;

/**
 * Created by 徐源茂 on 2018/3/24.
 */

public class College {
    private Integer collegeId;//院校Id
    private String name;//学校名称
    private String formerName;//学校曾用名
    private String province;//学校所在省份
    private String grade;//学校类型
    private String property;//学校属性
    private Boolean directlyUnder;//是否教育部直属
    private String runNature;//办学性质
    private String intro;//学校简介
    private Integer code;//学校编码
    private Integer ranking;//学校排名
    private String website;//学校官网
    private String telephone;//招办电话
    private String address;//详细地址
    private String mailbox;//邮箱
    public College() {
        super();
    }
    public Integer getCollegeId() {
        return collegeId;
    }
    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFormerName() {
        return formerName;
    }
    public void setFormerName(String formerName) {
        this.formerName = formerName;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public Boolean getDirectlyUnder() {
        return directlyUnder;
    }
    public void setDirectlyUnder(Boolean directlyUnder) {
        this.directlyUnder = directlyUnder;
    }
    public String getRunNature() {
        return runNature;
    }
    public void setRunNature(String runNature) {
        this.runNature = runNature;
    }
    public String getIntro() {
        return intro;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public Integer getRanking() {
        return ranking;
    }
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getMailbox() {
        return mailbox;
    }
    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }
}
