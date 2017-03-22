package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class ShoppingListPresenter {


    @Nonnull
    private final Observable<List<BaseAdapterItem>> shoppingListObservable;

    @Inject
    ShoppingListPresenter(@Nonnull final ShoppingListDao dao) {
        shoppingListObservable = dao.getShoppingListObservable()
                .map(toAdapterItems());
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
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return false;
        }
    }

}
