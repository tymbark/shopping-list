package com.damianmichalak.shopping_list.model.api_models;

public class Product {

    private String id;
    private String name;
    private long dateAdded;
    private long datePurchased;

    public Product() {
    }

    public Product(String id, String name, long dateAdded, long datePurchased) {
        this.id = id;
        this.name = name;
        this.dateAdded = dateAdded;
        this.datePurchased = datePurchased;
    }

    public String getId() {
        return id;
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

    public Product purchased() {
        return new Product(id, name, dateAdded, System.currentTimeMillis());
    }

    public Product withId(String key) {
        return new Product(key, name, dateAdded, datePurchased);
    }

}
