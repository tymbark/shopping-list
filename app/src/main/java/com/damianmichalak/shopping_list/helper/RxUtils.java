package com.damianmichalak.shopping_list.helper;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;


public class RxUtils {

    @Nonnull
    public static <T> Observable<T> createObservableForReference(@Nonnull final DatabaseReference reference, @Nonnull final EventsWrapper eventsWrapper, final Class<T> type) {
        return Observable.fromEmitter(asyncEmitter -> {
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
        }, AsyncEmitter.BackpressureMode.LATEST);
//                .doOnUnsubscribe(() -> reference.removeEventListener(eventsWrapper.getFirebaseListener()));
    }

    @Nonnull
    public static <T> Observable<List<T>> createObservableListForReference(@Nonnull final DatabaseReference reference, @Nonnull final EventsWrapper eventsWrapper, final Class<T> type) {
        return Observable.fromEmitter((Action1<AsyncEmitter<List<T>>>) asyncEmitter -> {
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
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(() -> reference.removeEventListener(eventsWrapper.getFirebaseListener()));
    }

    @Nonnull
    public static <T> Observable<Map<String, T>> createObservableMapForReference(@Nonnull final DatabaseReference reference, @Nonnull final EventsWrapper eventsWrapper, final Class<T> type) {
        return Observable.fromEmitter((Action1<AsyncEmitter<Map<String, T>>>) asyncEmitter -> {
            eventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final LinkedHashMap<String, T> output = new LinkedHashMap<>();
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        output.put(next.getKey(), next.getValue(type));
                    }
                    asyncEmitter.onNext(output);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    asyncEmitter.onError(new Throwable(databaseError.getMessage()));
                }
            });
            reference.addValueEventListener(eventsWrapper.getFirebaseListener());
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(() -> reference.removeEventListener(eventsWrapper.getFirebaseListener()));
    }

}
