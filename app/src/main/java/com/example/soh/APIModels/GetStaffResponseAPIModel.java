package com.example.soh.APIModels;

import com.example.soh.Models.Account;
import com.example.soh.Models.Staff;

import java.io.Serializable;
import java.util.List;

public class GetStaffResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<Staff> data;

    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<Staff> getData() { return data; }
    public void setData(List<Staff> value) { this.data = value; }
}
