package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.ShoppingItem;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;

public class ShoppingListPresenter {

    @Nonnull
    private final Observable<List<BaseAdapterItem>> shoppingListObservable;

    @Inject
    ShoppingListPresenter() {
        shoppingListObservable = getFakeItems();
    }

    private Observable<List<BaseAdapterItem>> getFakeItems() {
        List<BaseAdapterItem> list = new ArrayList<>();
        list.add(new ShoppingListItem(new ShoppingItem("Milk", "3%")));
        list.add(new ShoppingListItem(new ShoppingItem("Bread", "with seeds")));
        list.add(new ShoppingListItem(new ShoppingItem("Water", "mineral")));
        list.add(new ShoppingListItem(new ShoppingItem("Cheese", "at least 10 pieces")));
        list.add(new ShoppingListItem(new ShoppingItem("Corn flakes", "")));
        list.add(new ShoppingListItem(new ShoppingItem("Chocolate", "milka")));
        list.add(new ShoppingListItem(new ShoppingItem("Popcorn", "")));
        list.add(new ShoppingListItem(new ShoppingItem("Coca Cola", "")));
        list.add(new ShoppingListItem(new ShoppingItem("Apples", "1kg")));
        list.add(new ShoppingListItem(new ShoppingItem("Ketchup", "")));
        list.add(new ShoppingListItem(new ShoppingItem("Pizza", "frozen, with bacon")));
        list.add(new ShoppingListItem(new ShoppingItem("Toothpaste", "")));
        list.add(new ShoppingListItem(new ShoppingItem("Toilet paper", "")));
        list.add(new ShoppingListItem(new ShoppingItem("Plastic bags", "")));
        return Observable.just(list);
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getShoppingListObservable() {
        return shoppingListObservable;
    }

    public class ShoppingListItem implements BaseAdapterItem {

        @Nonnull
        private final ShoppingItem shoppingItem;

        public ShoppingListItem(@Nonnull ShoppingItem shoppingItem) {
            this.shoppingItem = shoppingItem;
        }

        @Nonnull
        public ShoppingItem getShoppingItem() {
            return shoppingItem;
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
