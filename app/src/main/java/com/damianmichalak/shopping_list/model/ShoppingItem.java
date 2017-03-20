package com.damianmichalak.shopping_list.model;

@Deprecated
public class ShoppingItem {

    private final String name;
    private final String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ShoppingItem(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
