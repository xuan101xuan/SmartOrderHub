package com.example.soh.APIModels;

import com.example.soh.Models.Account;

import java.io.Serializable;
import java.util.List;

public class GetAccountResponseAPIModel implements Serializable {
    private boolean status;
    private String message;
    private List<Account> data;

    public boolean getStatus() { return status; }
    public void setStatus(boolean value) { this.status = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public List<Account> getData() { return data; }
    public void setData(List<Account> value) { this.data = value; }
}
