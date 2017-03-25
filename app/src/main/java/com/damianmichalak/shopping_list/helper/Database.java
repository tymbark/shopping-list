package com.damianmichalak.shopping_list.helper;


import com.damianmichalak.shopping_list.model.UserPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class Database {

    private final String DATABASE_NAME = "shopping_lists/";

    private final FirebaseDatabase firebaseDatabase;
    private UserPreferences userPreferences;

    @Inject
    public Database(@Nonnull final UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public DatabaseReference productsReference() {
        return firebaseDatabase.getReference(DATABASE_NAME + userPreferences.getUid() + "/products");
    }

    public DatabaseReference shoppingListReference() {
        return firebaseDatabase.getReference(DATABASE_NAME + userPreferences.getUid());
    }

    public DatabaseReference userReference() {
        return firebaseDatabase.getReference(DATABASE_NAME + userPreferences.getUid());
    }

}
