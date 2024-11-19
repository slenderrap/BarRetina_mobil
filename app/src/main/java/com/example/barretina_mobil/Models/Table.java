package com.example.barretina_mobil.Models;

public class Table {
    private int number;
    private String waiterName;
    private boolean paid;
    private boolean free;

    public Table(int number, String waiterName, boolean paid, boolean free) {
        this.number = number;
        this.waiterName = waiterName;
        this.paid = paid;
        this.free = free;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }
} 