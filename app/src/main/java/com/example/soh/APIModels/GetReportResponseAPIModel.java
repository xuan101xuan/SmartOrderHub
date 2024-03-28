package com.example.soh.APIModels;

import com.example.soh.Models.Kitchen;
import com.example.soh.Models.Report;

import java.io.Serializable;
import java.util.List;

public class GetReportResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<Report> data;



    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<Report> getData() {
        return data;
    }

    public void setData(List<Report> data) {
        this.data = data;
    }
}
