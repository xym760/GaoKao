package com.nxist.gaokao.Module;

/**
 * Created by 徐源茂 on 2018/3/26.
 */

public class CollegeDetailItem {
    private String itemName;
    private String itemContent;
    public CollegeDetailItem(String itemName,String itemContent){
        this.itemName=itemName;
        this.itemContent=itemContent;
    }
    public String getItemName(){
        return itemName;
    }
    public String getItemContent(){
        return itemContent;
    }
}
