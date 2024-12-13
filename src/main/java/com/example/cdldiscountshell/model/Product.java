package com.example.cdldiscountshell.model;

public class Product {
    private String sku;
    private String name;
    private int unitPrice;

    public Product(String sku, String name, int unitPrice) {
        this.sku = sku;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }
}
