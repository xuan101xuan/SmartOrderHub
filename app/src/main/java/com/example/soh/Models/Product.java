package com.example.soh.Models;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

public class Product implements Serializable {

    private String idProduct;
    private String productName;
    private double productPrice;
    private String imageUrl;
//    private File imageUrl;
    private int productType;
    private int productStatus;
    private int productQuantity;
//    private String productNote = "Không";

    public Product() {
//        this.productName = productName;
//        this.productPrice = productPrice;
//        this.imageUrl = imageUrl;
//        this.productType = productType;
//        this.productStatus = productStatus;
//        this.productQuantity = productQuantity;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
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

//    public File getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(File imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

//    public String getProductNote() {
//        return productNote;
//    }
//
//    public void setProductNote(String productNote) {
//        this.productNote = productNote;
//    }
}

