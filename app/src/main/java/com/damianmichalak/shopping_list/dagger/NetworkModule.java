package com.damianmichalak.shopping_list.dagger;

import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@dagger.Module
public class NetworkModule {

    @Named("UI")
    @Provides
    Scheduler provideUiScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Named("IO")
    @Provides
    Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @Nonnull
    public FirebaseDatabase provideFirebase() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        return firebaseDatabase;
    }

}
