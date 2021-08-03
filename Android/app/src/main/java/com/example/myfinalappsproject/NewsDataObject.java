package com.example.myfinalappsproject;

public class NewsDataObject {

    private String SystemNotification;
    private int Category;

    public NewsDataObject() {
    }


    public NewsDataObject(String systemNotification) {
        SystemNotification = systemNotification;
    }

    public int getCategory() {
        return Category;
    }

    public String getSystemNotification() {
        return SystemNotification;
    }

    public void setSystemNotification(String systemNotification) {
        SystemNotification = systemNotification;
    }

    public void setCategory(int category) {
        Category = category;
    }
}
