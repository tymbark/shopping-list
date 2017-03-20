package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.Product;
import com.damianmichalak.shopping_list.model.ShoppingItem;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class ShoppingListPresenter {


    @Nonnull
    private final Observable<List<BaseAdapterItem>> shoppingListObservable;

    @Inject
    ShoppingListPresenter(@Nonnull final ShoppingListDao dao) {
        shoppingListObservable = dao.getListObservable()
                .map(toAdapterItems());
    }

    private Func1<ShoppingList, List<BaseAdapterItem>> toAdapterItems() {
        return new Func1<ShoppingList, List<BaseAdapterItem>>() {
            @Override
            public List<BaseAdapterItem> call(ShoppingList shoppingList) {
                final List<BaseAdapterItem> items = new ArrayList<>();

                for (Product product : shoppingList.getProducts()) {
                    items.add(new ShoppingListItem(product));
                }

                return items;
            }
        };
    }

//    private Observable<List<BaseAdapterItem>> getFakeItems() {
//        List<BaseAdapterItem> list = new ArrayList<>();
//        list.add(new ShoppingListItem(new ShoppingItem("Milk", "3%")));
//        list.add(new ShoppingListItem(new ShoppingItem("Bread", "with seeds")));
//        list.add(new ShoppingListItem(new ShoppingItem("Water", "mineral")));
//        list.add(new ShoppingListItem(new ShoppingItem("Cheese", "at least 10 pieces")));
//        list.add(new ShoppingListItem(new ShoppingItem("Corn flakes", "")));
//        list.add(new ShoppingListItem(new ShoppingItem("Chocolate", "milka")));
//        list.add(new ShoppingListItem(new ShoppingItem("Popcorn", "")));
//        list.add(new ShoppingListItem(new ShoppingItem("Coca Cola", "")));
//        list.add(new ShoppingListItem(new ShoppingItem("Apples", "1kg")));
//        list.add(new ShoppingListItem(new ShoppingItem("Ketchup", "")));
//        list.add(new ShoppingListItem(new ShoppingItem("Pizza", "frozen, with bacon")));
//        list.add(new ShoppingListItem(new ShoppingItem("Toothpaste", "")));
//        list.add(new ShoppingListItem(new ShoppingItem("Toilet paper", "")));
//        list.add(new ShoppingListItem(new ShoppingItem("Plastic bags", "")));
//        return Observable.just(list);
//    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getShoppingListObservable() {
        return shoppingListObservable;
    }

    public class ShoppingListItem implements BaseAdapterItem {

        @Nonnull
        private final Product product;

        public ShoppingListItem(@Nonnull Product product) {
            this.product = product;
        }

        @Nonnull
        public Product getProduct() {
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
