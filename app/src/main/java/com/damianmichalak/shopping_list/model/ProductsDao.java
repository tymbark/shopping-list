package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.References;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ProductsDao {

    @Nonnull
    private final Database<Product> methods;
    @Nonnull
    private final Observable<DatabaseReference> referenceObservable;
    @Nonnull
    private final Observable<Map<String, Product>> productsObservable;

    @Inject
    public ProductsDao(@Nonnull final References references,
                       @Nonnull final Database<Product> database,
                       @Nonnull final CurrentListDao currentListDao) {
        this.methods = database;

        referenceObservable = currentListDao.getCurrentListKeyObservable()
                .filter(uid -> uid != null)
                .map(references::productsReference)
                .replay(1)
                .refCount();

        productsObservable = referenceObservable.switchMap(reference -> database.itemsAsMap(reference, Product.class))
                .replay(1)
                .refCount();
    }

    public Observable<Boolean> addNewItemObservable(final Product product) {
        return referenceObservable.switchMap(reference -> methods.put(product, reference));
    }

    public Observable<Boolean> removeItemByKeyObservable(final String key) {
        return referenceObservable.switchMap(reference -> methods.remove(key, reference));
    }

    @Nonnull
    public Observable<Map<String, Product>> getProductsObservable() {
        return productsObservable;
    }
}
