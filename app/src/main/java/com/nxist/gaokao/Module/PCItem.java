package com.nxist.gaokao.Module;

/**
 * Created by 徐源茂 on 2018/3/27.
 */

public class PCItem {
    private String name;
    private int imageId;
    private String result;
    public PCItem(String name,int imageId,String result){
        this.name=name;
        this.imageId=imageId;
        this.result=result;
    }
    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }

    public String getResult(){
        return result;
    }

    public void setResult(String result){
        this.result=result;
    }
}
