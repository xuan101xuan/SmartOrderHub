package com.example.soh.Models;

import java.io.Serializable;
import java.text.ParseException;
import java.util.UUID;

public class Kitchen implements Comparable<Kitchen> {

    private UUID idKitchen;
    private String productName;
    private double productPrice;
    private String imageUrl;
    private int tableNum;
    private int createdNumber;
    private int productQuantity;
    private String productNote;

    public UUID getIdKitchen() {
        return idKitchen;
    }

    public void setIdKitchen(UUID idKitchen) {
        this.idKitchen = idKitchen;
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
    public int getTableNum() {
        return tableNum;
    }

    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductNote() {
        return productNote;
    }

    public void setProductNote(String productNote) {
        this.productNote = productNote;
    }

    public int getCreatedNumber() {
        return createdNumber;
    }

    public void setCreatedNumber(int createdNumber) {
        this.createdNumber = createdNumber;
    }
    @Override
    public int compareTo(Kitchen other) {
        return Integer.compare(this.getCreatedNumber(), other.getCreatedNumber());
    }
    public boolean isSameKitchen(Kitchen other) {
        return this.productName.equals(other.productName);
    }

    public void increaseQuantity() {
        this.productQuantity++;
    }

}
