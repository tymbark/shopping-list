package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.ProductsDatabase;
import com.damianmichalak.shopping_list.model.api_models.Product;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ProductsDao {

    @Nonnull
    private final Observable<Map<String, Product>> productsObservable;
    @Nonnull
    private final ProductsDatabase productsDatabase;
    @Nonnull
    private final Observable<String> currentListKeyObservable;

    @Inject
    public ProductsDao(@Nonnull final ProductsDatabase productsDatabase,
                       @Nonnull final CurrentListDao dao) {
        this.productsDatabase = productsDatabase;

        currentListKeyObservable = dao.getCurrentListKeyObservable()
                .filter(uid -> uid != null)
                .replay(1)
                .refCount();

        productsObservable = currentListKeyObservable
                .switchMap(productsDatabase::products)
                .replay(1)
                .refCount();

    }

    public Observable<Boolean> addNewItemObservable(final Product product) {
        return currentListKeyObservable.flatMap(uid -> productsDatabase.put(product, uid));
    }

    public Observable<Boolean> removeItemByKeyObservable(final String key) {
        return currentListKeyObservable.flatMap(uid -> productsDatabase.remove(key, uid));
    }

    @Nonnull
    public Observable<Map<String, Product>> getProductsObservable() {
        return productsObservable;
    }
}
