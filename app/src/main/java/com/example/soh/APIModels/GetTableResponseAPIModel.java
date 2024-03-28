package com.example.soh.APIModels;

import com.example.soh.Models.TableNumber;

import java.io.Serializable;
import java.util.List;

public class GetTableResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<TableNumber> data;

    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<TableNumber> getData() { return data; }
    public void setData(List<TableNumber> value) { this.data = value; }
}
