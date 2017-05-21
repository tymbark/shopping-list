package com.damianmichalak.shopping_list.dagger;

import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import dagger.Provides;

@dagger.Module
public class NetworkModule {

    @Provides
    @Singleton
    @Nonnull
    public FirebaseDatabase provideFirebase() {
        return FirebaseDatabase.getInstance();
    }

}
