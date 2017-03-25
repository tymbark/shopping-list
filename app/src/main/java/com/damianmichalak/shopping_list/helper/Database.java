package com.damianmichalak.shopping_list.helper;


import com.damianmichalak.shopping_list.model.UserPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class Database {

    private final String DATABASE_NAME = "shopping_lists/";

    @Nonnull
    private final UserPreferences userPreferences;
    @Nonnull
    private final FirebaseDatabase firebaseDatabase;

    @Inject
    public Database(@Nonnull final UserPreferences userPreferences,
                    @Nonnull final FirebaseDatabase firebaseDatabase) {
        this.userPreferences = userPreferences;
        this.firebaseDatabase = firebaseDatabase;
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
