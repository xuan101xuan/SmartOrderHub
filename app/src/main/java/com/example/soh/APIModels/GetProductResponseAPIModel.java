package com.example.soh.APIModels;

import com.example.soh.Models.Product;

import java.io.Serializable;
import java.util.List;

public class GetProductResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<Product> data;

    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<Product> getData() { return data; }
    public void setData(List<Product> value) { this.data = value; }
}
