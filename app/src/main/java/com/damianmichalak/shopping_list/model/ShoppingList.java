package com.damianmichalak.shopping_list.model;

import com.google.firebase.database.PropertyName;

import javax.annotation.Nonnull;

public class ShoppingList {

    @Nonnull
    @PropertyName("created_at")
    private long createdAt;

    @Nonnull
    private String name;

    public long getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

}
