package com.example.soh.Models;

import java.io.Serializable;
import java.util.UUID;

public class Report implements Serializable {

    private UUID idRep;
    private String productName;
    private double productPrice;
    private int productQuantity;
    private String reportDate;

    public UUID getIdRep() {
        return idRep;
    }

    public void setIdRep(UUID idRep) {
        this.idRep = idRep;
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

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}
