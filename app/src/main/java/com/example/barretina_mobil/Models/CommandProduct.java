package com.example.barretina_mobil.Models;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONObject;
public class CommandProduct {
    private int id;
    private String name;
    private double unitPrice;
    private int quantity;

    public CommandProduct(ProductInfo product, int quantity) {
        this.id = product.getId();
        this.name = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public CommandProduct(int id, String name, double unitPrice, int quantity) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("name", name);
            json.put("unitPrice", unitPrice);
            json.put("quantity", quantity);
            return json;
        } catch (Exception e) {
            Log.e("CommandProduct", "Error converting to JSON", e);
            return null;
        }
    }
}
