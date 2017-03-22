package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;

public class ShoppingListPresenter {


    @Nonnull
    private final Observable<List<BaseAdapterItem>> shoppingListObservable;
    @Nonnull
    private final PublishSubject<String> removeItemSubject = PublishSubject.create();
    @Nonnull
    private final ShoppingListDao dao;

    @Inject
    ShoppingListPresenter(@Nonnull final ShoppingListDao dao) {
        shoppingListObservable = dao.getProductsObservable()
                .map(toAdapterItems3());
        this.dao = dao;

        removeItemSubject
                .flatMap(new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String itemKey) {
                        return dao.removeItemByKeyObservable(itemKey);
                    }
                })
                .subscribe();

    }

    private Func1<ShoppingList, List<BaseAdapterItem>> toAdapterItems() {
        return new Func1<ShoppingList, List<BaseAdapterItem>>() {
            @Override
            public List<BaseAdapterItem> call(ShoppingList shoppingList) {
                final List<BaseAdapterItem> items = new ArrayList<>();

                final Map<String, String> products = shoppingList.getProducts();
                if (products != null) {
                    for (String product : products.values()) {
                        items.add(new ShoppingListItem(product));
                    }
                }

                return items;
            }
        };
    }

    private Func1<List<String>, List<BaseAdapterItem>> toAdapterItems2() {
        return new Func1<List<String>, List<BaseAdapterItem>>() {
            @Override
            public List<BaseAdapterItem> call(List<String> products) {
                final List<BaseAdapterItem> items = Lists.newArrayList();

                if (products != null) {
                    for (String product : products) {
                        items.add(new ShoppingListItem(product));
                    }
                }

                return items;
            }
        };
    }

    private Func1<Map<String, String>, List<BaseAdapterItem>> toAdapterItems3() {
        return new Func1<Map<String, String>, List<BaseAdapterItem>>() {
            @Override
            public List<BaseAdapterItem> call(Map<String, String> products) {
                final List<BaseAdapterItem> items = Lists.newArrayList();

                if (products != null) {
                    for (String key : products.keySet()) {
                        items.add(new ShoppingListItemWithKey(key, products.get(key)));
                    }

                }

                return items;
            }
        };
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
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    removeItemSubject.onNext(id);
                }
            });
        }
    }

}
