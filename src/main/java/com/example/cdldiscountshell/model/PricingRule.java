package com.example.cdldiscountshell.model;

public class PricingRule {
    private Product product;
    private int specialQuantity;
    private int specialPrice;

    public PricingRule(Product product, int specialQuantity, int specialPrice) {
        this.product = product;
        this.specialQuantity = specialQuantity;
        this.specialPrice = specialPrice;
    }

    public Product getProduct() {
        return product;
    }

    public int getSpecialQuantity() {
        return specialQuantity;
    }

    public int getSpecialPrice() {
        return specialPrice;
    }
}
