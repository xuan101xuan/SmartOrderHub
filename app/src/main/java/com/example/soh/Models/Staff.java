package com.example.soh.Models;

import java.io.Serializable;
import java.text.Collator;
import java.util.UUID;

public class Staff implements Serializable, Comparable<Staff> {

    private String idStaff;
    private String nameStaff;
    private String phoneNum;
    private String address;
    private String role;

    public Staff() {
        this.idStaff = idStaff;
        this.nameStaff = nameStaff;
        this.phoneNum = phoneNum;
        this.address = address;
        this.role = role;
    }

    public String getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(String idStaff) {
        this.idStaff = idStaff;
    }

    public String getNameStaff() {
        return nameStaff;
    }

    public void setNameStaff(String nameStaff) {
        this.nameStaff = nameStaff;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Phương thức compareTo để sắp xếp theo tên cuối cùng và bảng chữ cái
    @Override
    public int compareTo(Staff otherStaff) {
        // Lấy tên cuối cùng từ mỗi nhân viên
        String lastName1 = getLastWord(this.nameStaff);
        String lastName2 = getLastWord(otherStaff.nameStaff);

        // Sử dụng Collator để so sánh theo bảng chữ cái
        return Collator.getInstance().compare(lastName1, lastName2);
    }

    // Phương thức để lấy tên cuối cùng từ chuỗi
    private String getLastWord(String fullName) {
        String[] words = fullName.split("\\s+");
        return words[words.length - 1];
    }
}

