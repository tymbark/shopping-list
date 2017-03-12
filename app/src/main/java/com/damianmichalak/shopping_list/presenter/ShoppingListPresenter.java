package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;

public class ShoppingListPresenter {

    @Nonnull
    private final Observable<List<ShoppingItem>> shoppingListObservable;

    @Inject
    ShoppingListPresenter() {
        shoppingListObservable = getFakeItems();
    }

    private Observable<List<ShoppingItem>> getFakeItems() {
        List<ShoppingItem> list = new ArrayList<>();
        list.add(new ShoppingItem("Milk", "3%"));
        list.add(new ShoppingItem("Bread", "with seeds"));
        list.add(new ShoppingItem("Water", "mineral"));
        list.add(new ShoppingItem("Cheese", "at least 10 pieces"));
        list.add(new ShoppingItem("Corn flakes", ""));
        list.add(new ShoppingItem("Chocolate", "milka"));
        list.add(new ShoppingItem("Popcorn", ""));
        list.add(new ShoppingItem("Coca Cola", ""));
        list.add(new ShoppingItem("Apples", "1kg"));
        list.add(new ShoppingItem("Ketchup", ""));
        list.add(new ShoppingItem("Pizza", "frozen, with bacon"));
        list.add(new ShoppingItem("Toothpaste", ""));
        list.add(new ShoppingItem("Toilet paper", ""));
        list.add(new ShoppingItem("Plastic bags", ""));
        return Observable.just(list);
    }

    @Nonnull
    public Observable<List<ShoppingItem>> getShoppingListObservable() {
        return shoppingListObservable;
    }
}
