package com.example.soh.APIModels;

import com.example.soh.Models.Cashier;

import java.io.Serializable;
import java.util.List;

public class GetCashierResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<Cashier> data;

    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<Cashier> getData() { return data; }
    public void setData(List<Cashier> value) { this.data = value; }
}
