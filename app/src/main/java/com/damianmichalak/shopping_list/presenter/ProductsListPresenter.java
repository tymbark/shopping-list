package com.damianmichalak.shopping_list.presenter;


import android.util.Log;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.damianmichalak.shopping_list.model.ProductsDao;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.damianmichalak.shopping_list.model.api_models.ShoppingList;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ProductsListPresenter {

    @Nonnull
    private final Observable<String> listNameObservable;
    @Nonnull
    private final Observable<List<BaseAdapterItem>> currentShoppingListObservable;
    @Nonnull
    private final PublishSubject<Product> removeItemSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> newShoppingListObserver = PublishSubject.create();
    @Nonnull
    private final Observable<Boolean> emptyListObservable;
    @Nonnull
    private final Observable<Boolean> noListsObservable;
    @Nonnull
    private final Observable<Object> showNewListDialogObservable;
    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();

    @Inject
    ProductsListPresenter(@Nonnull final ProductsDao productsDao,
                          @Nonnull final CurrentListDao currentListDao,
                          @Nonnull final ListsDao listsDao,
                          @Named("AddListClickObservable") Observable<Void> addListClickObservable) {

        listNameObservable = currentListDao.getCurrentListObservable()
                .filter(list -> list != null)
                .map(ShoppingList::getName);

        showNewListDialogObservable = addListClickObservable.map(v -> null);

        currentShoppingListObservable = productsDao.getProductsObservable()
                .map(toAdapterItems());

        noListsObservable = listsDao.getAvailableListsObservable().map(Map::isEmpty);

        emptyListObservable = Observable
                .combineLatest(currentShoppingListObservable.map(List::isEmpty), noListsObservable,
                        (currentListEmpty, noLists) -> currentListEmpty && !noLists)
                .share();

        subscription.set(Subscriptions.from(
                removeItemSubject
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                Log.d("CHUJ", "subscribe");
                            }
                        })
                        .doOnUnsubscribe(new Action0() {
                            @Override
                            public void call() {
                                Log.d("CHUJ", "UNsubscribe");
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Log.d("CHUJ", "completed");
                            }
                        })
                        .doOnNext(new Action1<Product>() {
                            @Override
                            public void call(Product product) {
                                Log.d("CHUJ", "next");
                            }
                        })
                        .flatMap(productsDao::removeItemByKeyObservable)
                        .subscribe(),
                newShoppingListObserver
                        .flatMap(listsDao::addNewListObservable)
                        .subscribe()
        ));
    }

    private Func1<List<Product>, List<BaseAdapterItem>> toAdapterItems() {
        return products -> {
            final List<BaseAdapterItem> items = Lists.newArrayList();

            if (products != null) {
                for (Product product : products) {
                    items.add(new ShoppingListItemWithKey(product));
                }
            }

            return items;
        };
    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

    @Nonnull
    public Observable<Object> getShowNewListDialogObservable() {
        return showNewListDialogObservable;
    }

    @Nonnull
    public Observable<Boolean> getNoListsObservable() {
        return noListsObservable;
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
    public Observable<Boolean> getFloatingActionButtonObservable() {
        return noListsObservable.map(o -> !o);
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getCurrentShoppingListObservable() {
        return currentShoppingListObservable;
    }

    @Nonnull
    public Observer<String> getNewShoppingListObserver() {
        return newShoppingListObserver;
    }

    public class ShoppingListItemWithKey implements BaseAdapterItem {

        @Nonnull
        private final Product product;

        @Nonnull
        public Product getProduct() {
            return product;
        }

        public ShoppingListItemWithKey(@Nonnull Product product) {
            this.product = product;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShoppingListItemWithKey)) return false;
            ShoppingListItemWithKey that = (ShoppingListItemWithKey) o;
            return Objects.equal(product, that.product);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(product);
        }

        @Override
        public long adapterId() {
            return 0;
        }

        @Override
        public boolean matches(@Nonnull BaseAdapterItem item) {
            return item instanceof ShoppingListItemWithKey && ((ShoppingListItemWithKey) item).product.getId().equals(product.getId());
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return equals(item);
        }

        public Observer<Object> removeItem() {
            return Observers.create(o -> removeItemSubject.onNext(product));
        }
    }

}
