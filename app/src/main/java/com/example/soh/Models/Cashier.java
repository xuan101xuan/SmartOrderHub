package com.example.soh.Models;

import java.io.Serializable;
import java.util.UUID;

public class Cashier implements Serializable {

    private UUID idCashier;
    private String productName;
    private double productPrice;
    private int productQuantity;
    private int tableNum;


    public UUID getIdCashier() {
        return idCashier;
    }

    public void setIdCashier(UUID idCashier) {
        this.idCashier = idCashier;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getTableNum() {
        return tableNum;
    }

    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }
}
