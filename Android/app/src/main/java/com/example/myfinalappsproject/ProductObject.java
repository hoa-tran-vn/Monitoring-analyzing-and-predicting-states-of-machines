package com.example.myfinalappsproject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

public class ProductObject {
    private int ProductID;
    private String ProductName;
    private String ProductDiscp;
    private LocalDateTime ProductStartTime;
    private int ProductAmmount;

    public ProductObject() {
    }

    public String getProductDiscp() {
        return ProductDiscp;
    }

    public int getProductID() {
        return ProductID;
    }

    public String getProductName() {
        return ProductName;
    }

    public LocalDateTime getProductStartTime() {
        return ProductStartTime;
    }

    public int getProductAmmount() {
        return ProductAmmount;
    }

    public void setProductDiscp(String productDiscp) {
        ProductDiscp = productDiscp;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setProductStartTime(LocalDateTime productStartTime) {
        ProductStartTime = productStartTime;
    }

    public void setProductAmmount(int productAmmount) {
        ProductAmmount = productAmmount;
    }

}
