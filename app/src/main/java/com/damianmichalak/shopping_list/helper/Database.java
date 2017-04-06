package com.damianmichalak.shopping_list.helper;


import com.damianmichalak.shopping_list.model.UserPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class Database {

    private final String DB_SHOPPING_LIST = "shopping_lists/";
    private final String DB_USERS = "users/";

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

    @Deprecated
    public DatabaseReference DEPRECATEDproductsReference() {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST + userPreferences.getUid() + "/products");
    }

    @Deprecated
    public DatabaseReference DEPRECATEDshoppingListReference() {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST + userPreferences.getUid());
    }

    public DatabaseReference allListsReference() {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST);
    }

    public DatabaseReference userReference() {
        return firebaseDatabase.getReference(DB_USERS + userPreferences.getUid());
    }

    public DatabaseReference userCreatedReference() {
        return firebaseDatabase.getReference(DB_USERS + userPreferences.getUid() + "/created");
    }

    public DatabaseReference userNameReference() {
        return firebaseDatabase.getReference(DB_USERS + userPreferences.getUid() + "/name");
    }

    public DatabaseReference userListsReference() {
        return firebaseDatabase.getReference(DB_USERS + userPreferences.getUid() + "/shopping_list_access/");
    }

    public DatabaseReference singleListReference(String listUID) {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST + listUID);
    }

    public DatabaseReference singleListNameReference(String listUID) {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST + listUID + "/name");
    }

    public DatabaseReference productsReference(String listUID) {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST + listUID + "/products");
    }

    public DatabaseReference rootReference() {
        return firebaseDatabase.getReference();
    }

    public DatabaseReference shoppingListReference(String listUID) {
        return firebaseDatabase.getReference(DB_SHOPPING_LIST + listUID + "/products");
    }

    public static String path(DatabaseReference reference) {
        return reference.toString().replace(reference.getRoot().toString(), "");
    }

}
