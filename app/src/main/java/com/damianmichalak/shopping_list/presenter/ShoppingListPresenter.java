package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ProductsDao;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;

public class ShoppingListPresenter {

    @Nonnull
    private final Observable<String> listNameObservable;
    @Nonnull
    private final Observable<List<BaseAdapterItem>> shoppingListObservable;
    @Nonnull
    private final PublishSubject<String> removeItemSubject = PublishSubject.create();
    @Nonnull
    private final Observable<Boolean> emptyListObservable;

    @Inject
    ShoppingListPresenter(@Nonnull final ProductsDao productsDao,
                          @Nonnull final CurrentListDao currentListDao) {

        listNameObservable = currentListDao.getCurrentListObservable()
                .filter(list -> list != null)
                .map(ShoppingList::getName);

        shoppingListObservable = productsDao.getProductsObservable()
                .map(toAdapterItems());

        emptyListObservable = shoppingListObservable.map(List::isEmpty);

        removeItemSubject
                .flatMap(productsDao::removeItemByKeyObservable)
                .subscribe();

    }

    private Func1<Map<String, String>, List<BaseAdapterItem>> toAdapterItems() {
        return products -> {
            final List<BaseAdapterItem> items = Lists.newArrayList();

            if (products != null) {
                for (String key : products.keySet()) {
                    items.add(new ShoppingListItemWithKey(key, products.get(key)));
                }
            }

            return items;
        };
    }

    @Nonnull
    public Observable<String> getListNameObservable() {
        return listNameObservable;
    }

    @Nonnull
    public Observable<Boolean> getEmptyListObservable() {
        return emptyListObservable;
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getShoppingListObservable() {
        return shoppingListObservable;
    }

    public class ShoppingListItem implements BaseAdapterItem {

        @Nonnull
        private final String product;

        public ShoppingListItem(@Nonnull String product) {
            this.product = product;
        }

        @Nonnull
        public String getProduct() {
            return product;
        }

        @Override
        public long adapterId() {
            return 0;
        }

        @Override
        public boolean matches(@Nonnull BaseAdapterItem item) {
            return item instanceof ShoppingListItem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShoppingListItem)) return false;
            ShoppingListItem that = (ShoppingListItem) o;
            return Objects.equal(product, that.product);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(product);
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return equals(item);
        }
    }

    public class ShoppingListItemWithKey implements BaseAdapterItem {

        @Nonnull
        private final String id;
        @Nonnull
        private final String name;

        @Nonnull
        public String getId() {
            return name;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        public ShoppingListItemWithKey(@Nonnull String id, @Nonnull String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShoppingListItemWithKey)) return false;
            ShoppingListItemWithKey that = (ShoppingListItemWithKey) o;
            return Objects.equal(id, that.id) &&
                    Objects.equal(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, name);
        }

        @Override
        public long adapterId() {
            return 0;
        }

        @Override
        public boolean matches(@Nonnull BaseAdapterItem item) {
            return this.equals(item);
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return this.equals(item);
        }

        public Observer<Object> removeItem() {
            return Observers.create(o -> removeItemSubject.onNext(id));
        }
    }

}
