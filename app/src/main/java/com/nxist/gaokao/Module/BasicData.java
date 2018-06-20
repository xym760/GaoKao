package com.nxist.gaokao.Module;

/**
 * Created by 徐源茂 on 2018/3/23.
 */

public class BasicData {
    public static final String SERVER_ADDRESS="http://192.168.137.1:8080/gaokao/";//内网地址
    //public static final String SERVER_ADDRESS="http://47.94.250.232:8080/gaokao/";//外网地址
    public static final String SCHOOL_APPKEY="a9e80a015d1df96bbc4f140d7f0a6a4a";
    public static final String BAIDU_APPKEY="NglkcRDG6D4Khjjzp9C1cjFISyurlHoW";//百度地图appkey
    public static final String[] PROVINCE={"安徽","澳门","北京","重庆","福建","甘肃","贵州","广东","广西","河北","河南","黑龙江","湖北","湖南","海南","江苏","江西","吉林","辽宁","内蒙古","宁夏","青海","上海","山东","山西","陕西","四川","天津","新疆","西藏","香港","云南","台湾","浙江"};

    public static final String[] SHARE_ITEM={"QQ","微信","微信朋友圈"};
    //输出日志
    final String Fragmentlogd="Log.d(\"getActivity()\", \"信息:\")";

    //用户头像地址前缀
    public static final String USER_AVATAR_URL=SERVER_ADDRESS+"exchange/getUserAvatar/?picture=";
}