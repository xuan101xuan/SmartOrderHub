package com.example.soh.APIModels;

import com.example.soh.Models.Kitchen;

import java.io.Serializable;
import java.util.List;

public class GetKitchenResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<Kitchen> data;



    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<Kitchen> getData() {
        return data;
    }

    public void setData(List<Kitchen> data) {
        this.data = data;
    }
}
