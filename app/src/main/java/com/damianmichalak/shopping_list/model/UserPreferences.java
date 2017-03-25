package com.damianmichalak.shopping_list.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.damianmichalak.shopping_list.dagger.ForApplication;
import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserPreferences {

    private static final String PREFERENCES_NAME = "user_preferences";
    private static final String SUGGESTED_PRODUCTS = "suggested_products";
    private static final String USER_UID = "uid";
    private static final String CURRENT_LIST = "current_list";

    @Nonnull
    private final SharedPreferences preferences;
    @Nonnull
    private final Gson gson;

    @Inject
    public UserPreferences(@Nonnull @ForApplication Context context) {
        this.gson = new Gson();
        preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);

    }

    public void removeSuggestedProduct(String remove) {
        final String json = preferences.getString(SUGGESTED_PRODUCTS, null);

        final Type type = new TypeToken<Set<String>>() {
        }.getType();

        final Set<String> set;
        if (!Strings.isNullOrEmpty(json)) {
            set = gson.fromJson(json, type);
            set.remove(remove);
        } else {
            return;
        }

        final String newJson = gson.toJson(set);
        preferences.edit().putString(SUGGESTED_PRODUCTS, newJson).apply();
    }

    public void addProductSuggested(String string) {
        final String json = preferences.getString(SUGGESTED_PRODUCTS, null);

        final Type type = new TypeToken<Set<String>>() {
        }.getType();

        final Set<String> set;
        if (!Strings.isNullOrEmpty(json)) {
            set = gson.fromJson(json, type);
        } else {
            set = new HashSet<>();
        }
        set.add(string);

        final String newJson = gson.toJson(set);
        preferences.edit().putString(SUGGESTED_PRODUCTS, newJson).apply();
    }

    @Nonnull
    public Set<String> getSuggestedProducts() {
        final String json = preferences.getString(SUGGESTED_PRODUCTS, null);

        final Type type = new TypeToken<Set<String>>() {
        }.getType();

        final Set<String> set;
        if (!Strings.isNullOrEmpty(json)) {
            set = gson.fromJson(json, type);
        } else {
            set = new HashSet<>();
        }
        return set;
    }

    public void setUid(String uid) {
        preferences.edit().putString(USER_UID, uid).apply();
    }

    @Nullable
    public String getUid() {
        return preferences.getString(USER_UID, null);
    }

    public void setCurrentList(String uid) {
        preferences.edit().putString(CURRENT_LIST, uid).apply();
    }

    @Nullable
    public String getCurrentList() {
        return preferences.getString(CURRENT_LIST, null);
    }
}
