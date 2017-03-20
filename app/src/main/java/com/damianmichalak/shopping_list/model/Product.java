package com.damianmichalak.shopping_list.model;


public class Product {

    private String name;
    private String info;

    public Product(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {

        return name;
    }
}
