package com.example.barretina_mobil.Models;

import android.os.Parcel;

public class CommandProduct {
    private String name;
    private double unitPrice;
    private int quantity;

    public CommandProduct(ProductInfo product, int quantity) {
        this.name = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public CommandProduct(String name, double unitPrice, int quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getunitPrice() {
        return unitPrice;
    }

    public void setunitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
