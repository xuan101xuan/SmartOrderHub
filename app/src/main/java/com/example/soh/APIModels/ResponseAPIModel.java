package com.example.soh.APIModels;

import java.io.Serializable;

public class ResponseAPIModel implements Serializable {
    private boolean status;
    private String message;


    // Getter Methods

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Setter Methods

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
