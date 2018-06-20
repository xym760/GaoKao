package com.nxist.gaokao.Module;

public class UserItem {
    private String itemName;
    private String itemValue;

    public UserItem(String itemName,String itemValue){
        this.itemName=itemName;
        this.itemValue=itemValue;
    }

    public UserItem(String itemName){
        this.itemName=itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
}
