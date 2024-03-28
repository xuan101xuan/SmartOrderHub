package com.example.soh.Models;

import java.util.UUID;

public class Cartlist {
    private UUID idCart;
    private String productName;
    private double productPrice;
    private String imageUrl;
    private int productQuantity;
    private int tableNum;

    public Cartlist(UUID idCart, String productName, double productPrice, String imageUrl, int productQuantity, int tableNum) {
        this.idCart = idCart;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.productQuantity = productQuantity;
        this.tableNum = tableNum;
    }

    public UUID getIdCart() {
        return idCart;
    }

    public void setIdCart(UUID idCart) {
        this.idCart = idCart;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
