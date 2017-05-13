package com.damianmichalak.shopping_list.model.api_models;


public class Product {

    private String name;
    private String info;
    private long dateAdded;
    private long datePurchased;

    public Product(String name, String info, long dateAdded, long datePurchased) {
        this.name = name;
        this.info = info;
        this.dateAdded = dateAdded;
        this.datePurchased = datePurchased;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public long getDatePurchased() {
        return datePurchased;
    }

}
