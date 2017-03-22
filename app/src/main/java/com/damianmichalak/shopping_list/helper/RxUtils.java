package com.damianmichalak.shopping_list.helper;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;


public class RxUtils {

    @Nonnull
    public static <T> Observable<T> createObservableForReference(@Nonnull final DatabaseReference reference, @Nonnull final EventsWrapper eventsWrapper, final Class<T> type) {
        return Observable.fromEmitter(new Action1<AsyncEmitter<T>>() {
            @Override
            public void call(final AsyncEmitter<T> asyncEmitter) {
                eventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final T item = dataSnapshot.getValue(type);
                        asyncEmitter.onNext(item);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        asyncEmitter.onError(new Throwable(databaseError.getMessage()));
                    }
                });
                reference.addValueEventListener(eventsWrapper.getFirebaseListener());
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        reference.removeEventListener(eventsWrapper.getFirebaseListener());

                    }
                });
    }

    @Nonnull
    public static <T> Observable<List<T>> createObservableListForReference(@Nonnull final DatabaseReference reference, @Nonnull final EventsWrapper eventsWrapper, final Class<T> type) {
        return Observable.fromEmitter(new Action1<AsyncEmitter<List<T>>>() {
            @Override
            public void call(final AsyncEmitter<List<T>> asyncEmitter) {
                eventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final List<T> output = Lists.newArrayList();
                        for (DataSnapshot next : dataSnapshot.getChildren()) {
                            output.add(next.getValue(type));
                        }
                        asyncEmitter.onNext(output);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        asyncEmitter.onError(new Throwable(databaseError.getMessage()));
                    }
                });
                reference.addValueEventListener(eventsWrapper.getFirebaseListener());
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        reference.removeEventListener(eventsWrapper.getFirebaseListener());

                    }
                });
    }

}