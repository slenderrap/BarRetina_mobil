package com.example.barretina_mobil.Models;

import java.io.Serializable;

public class ProductInfo implements Serializable {
    private String name;
    private double price;

    public ProductInfo(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
} 