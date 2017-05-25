package com.damianmichalak.shopping_list.model.api_models;

public class Product {

    private String name;
    private long dateAdded;
    private long datePurchased;

    public Product() {
    }

    public Product(String name, long dateAdded, long datePurchased) {
        this.name = name;
        this.dateAdded = dateAdded;
        this.datePurchased = datePurchased;
    }

    public String getName() {
        return name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public long getDatePurchased() {
        return datePurchased;
    }

}
