package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShoppingList {

    @Nullable
    @PropertyName("products")
    private LinkedHashMap<String, String> products;

    @Nonnull
    @PropertyName("created_at")
    private long createdAt;

    @Nonnull
    private String name;

    @Nullable
    public Map<String, String> getProducts() {
        return products;
    }

    @Nullable
    public List<String> getProductsList() {
        if (products != null) {

            return new ArrayList<>(products.values());
        }
        return Lists.newArrayList();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

}
