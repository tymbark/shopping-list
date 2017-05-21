package com.damianmichalak.shopping_list.helper;


import com.damianmichalak.shopping_list.model.api_models.Product;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.AsyncEmitter;
import rx.Observable;

public class ProductsDatabase {

    @Nonnull
    private final References References;
    @Nonnull
    private final EventsWrapper eventsWrapper;

    @Inject
    ProductsDatabase(@Nonnull References References,
                     @Nonnull EventsWrapper eventsWrapper) {
        this.References = References;
        this.eventsWrapper = eventsWrapper;
    }

    public Observable<Boolean> put(Product product, String listId) {
        return Observable.fromEmitter(
                emitter -> References.productsReference(listId)
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
                emitter -> References
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

    public Observable<Map<String, Product>> products(String listId) {
        return RxUtils.createObservableMapForReference(References.productsReference(listId), eventsWrapper, Product.class);
    }

}
