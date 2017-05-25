package com.damianmichalak.shopping_list.model.api_models;

import com.google.firebase.database.PropertyName;

import javax.annotation.Nonnull;

public class ShoppingList {

    @Nonnull
    @PropertyName("date_created")
    private long dateCreated;
    @Nonnull
    private String name;
    @Nonnull
    private String id;

    @Nonnull
    public long getDateCreated() {
        return dateCreated;
    }

    public String getName() {
        return name;
    }

}
