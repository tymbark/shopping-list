package com.damianmichalak.shopping_list.helper;


import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

public class Database<T> {

    private final EventsWrapper mapEventsWrapper = new EventsWrapper();

    @Inject
    public Database() {
    }

    public Observable<Boolean> put(T obj, DatabaseReference reference) {
        return Observable.fromEmitter(
                emitter -> reference
                        .push()
                        .setValue(obj)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onNext(true);
                            } else {
                                emitter.onNext(false);
                            }
                        }),
                AsyncEmitter.BackpressureMode.LATEST);
    }

    public Observable<Boolean> remove(String key, DatabaseReference reference) {
        return Observable.fromEmitter(
                emitter -> reference
                        .child(key)
                        .removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onNext(true);
                            } else {
                                emitter.onNext(false);
                            }
                        }),
                AsyncEmitter.BackpressureMode.LATEST);
    }

    public Observable<T> get(String key, DatabaseReference reference, Class<T> type) {
        final EventsWrapper singleEventsWrapper = new EventsWrapper();
        return Observable.fromEmitter((Action1<AsyncEmitter<T>>) emitter -> {
            singleEventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final T object = dataSnapshot.getValue(type);
                    emitter.onNext(object);
                    emitter.onCompleted();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(new Throwable(databaseError.getMessage()));
                }
            });
            reference.child(key).addValueEventListener(singleEventsWrapper.getFirebaseListener());
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(() -> reference.child(key).removeEventListener(singleEventsWrapper.getFirebaseListener()));
    }

    public Observable<Map<String, T>> itemsAsMap(DatabaseReference reference, Class<T> type) {
        return itemsAsMap(reference, type, null);
    }

    public Observable<Map<String, T>> itemsAsMap(DatabaseReference reference, Class<T> type, @Nullable String orderByChild) {
        return Observable.fromEmitter((Action1<AsyncEmitter<Map<String, T>>>) emitter -> {
            mapEventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final LinkedHashMap<String, T> output = new LinkedHashMap<>();
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        final T object = next.getValue(type);
                        output.put(next.getKey(), object);
                    }
                    emitter.onNext(output);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    reference.removeEventListener(mapEventsWrapper.getFirebaseListener());
                    emitter.onError(new Throwable(databaseError.getMessage()));
                }
            });
            if (Strings.isNotNullAndNotEmpty(orderByChild)) {
                reference.orderByChild(orderByChild).addValueEventListener(mapEventsWrapper.getFirebaseListener());
            } else {
                reference.addValueEventListener(mapEventsWrapper.getFirebaseListener());
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(() -> reference.removeEventListener(mapEventsWrapper.getFirebaseListener()));
    }

}
