package com.damianmichalak.shopping_list.helper;


import com.damianmichalak.shopping_list.model.api_models.Product;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.AsyncEmitter;
import rx.Observable;

public class ProductsDatabase {

    @Nonnull
    private final Database database;

    @Inject
    public ProductsDatabase(@Nonnull Database database) {
        this.database = database;
    }

    public Observable<Boolean> put(Product product, String listId) {
        return Observable.fromEmitter(
                emitter -> database.productsReference(listId)
                        .push()
                        .setValue(product)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onNext(true);
                            } else {
                                emitter.onNext(false);
                            }
                        }),
                AsyncEmitter.BackpressureMode.LATEST);
    }

    public Observable<Boolean> remove(String key, String listId) {
        return Observable.fromEmitter(
                emitter -> database
                        .productsReference(listId)
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

}
