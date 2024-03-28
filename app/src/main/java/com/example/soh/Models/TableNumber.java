package com.example.soh.Models;

import java.io.Serializable;
import java.util.UUID;

public class TableNumber implements Serializable, Comparable<TableNumber> {

    private String idTable;
    private int tableNum;
    private int tableStatus;
    private int floor;



    public String getIdTable() {
        return idTable;
    }

    public void setIdTable(String idTable) {
        this.idTable = idTable;
    }

    public int getTableNum() {
        return tableNum;
    }

    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }

    public int getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(int tableStatus) {
        this.tableStatus = tableStatus;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public int compareTo(TableNumber other) {
        return Integer.compare(this.getTableNum(), other.getTableNum());
    }
}

