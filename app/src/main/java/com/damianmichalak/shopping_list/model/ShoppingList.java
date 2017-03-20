package com.damianmichalak.shopping_list.model;

import com.google.firebase.database.PropertyName;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class ShoppingList {

    @Nonnull
    private List<String> products;

    @Nonnull
    @PropertyName("created_at")
    private long createdAt;

    @Nonnull
    private String name;

    @Nonnull
    public List<String> getProducts() {
        return products;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

}
