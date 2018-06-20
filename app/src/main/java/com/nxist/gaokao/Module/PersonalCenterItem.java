package com.nxist.gaokao.Module;

/**
 * Created by 徐源茂 on 2018/3/25.
 */

public class PersonalCenterItem {
    private int imageId;
    private String name;
    public PersonalCenterItem(int imageId,String name){
        this.imageId=imageId;
        this.name=name;
    }
    public int getImageId(){
        return imageId;
    }
    public String getName(){
        return name;
    }
}
