package com.damianmichalak.shopping_list.dagger;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;

@Deprecated
@dagger.Module
public class NetworkModule {

    @Provides
    @Singleton
    @Nonnull
    public FirebaseDatabase provideFirebase() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @Singleton
    @Named("shopping_list")
    @Nonnull
    public DatabaseReference provideShoppingReference(@Nonnull FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference("shopping_lists/0");
    }

    @Provides
    @Singleton
    @Named("shopping_list/products")
    @Nonnull
    public DatabaseReference provideProductsReference(@Nonnull FirebaseDatabase firebaseDatabase, @Named("token") String token) {
        return firebaseDatabase.getReference("shopping_lists//products");
    }

}
