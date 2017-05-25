package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.References;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class ProductsDao {

    @Nonnull
    private final Database<Product> database;
    @Nonnull
    private final HistoryDao historyDao;
    @Nonnull
    private final Observable<DatabaseReference> referenceObservable;
    @Nonnull
    private final Observable<List<Product>> productsObservable;

    @Inject
    public ProductsDao(@Nonnull final References references,
                       @Nonnull final Database<Product> database,
                       @Nonnull final CurrentListDao currentListDao,
                       @Nonnull final HistoryDao historyDao) {
        this.database = database;
        this.historyDao = historyDao;

        referenceObservable = currentListDao.getCurrentListKeyObservable()
                .filter(uid -> uid != null)
                .map(references::productsReference)
                .replay(1)
                .refCount();

        productsObservable = referenceObservable
                .switchMap(reference -> database.itemsAsMap(reference, Product.class))
                .map(toList())
                .replay(1)
                .refCount();
    }

    private Func1<Map<String, Product>, List<Product>> toList() {
        return map -> {
            final ArrayList<Product> produts = Lists.newArrayList();
            for (String key : map.keySet()) {
                final Product product = map.get(key);
                produts.add(product.withId(key));
            }

            return produts;
        };
    }

    public Observable<Boolean> addNewItemObservable(final Product product) {
        return referenceObservable.switchMap(reference -> database.put(product, reference));
    }

    public Observable<?> removeItemByKeyObservable(final Product product) {
        return referenceObservable
                .flatMap(reference -> database.remove(product.getId(), reference))
                .flatMap(ignore -> historyDao.addNewItemObservable(product));
    }

    @Nonnull
    public Observable<List<Product>> getProductsObservable() {
        return productsObservable;
    }
}
