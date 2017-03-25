package com.damianmichalak.shopping_list.presenter;

import android.util.Log;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class DrawerFragmentPresenter {

    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<List<BaseAdapterItem>> listObservable;
    @Nonnull
    private final PublishSubject<String> setCurrentListSubejct = PublishSubject.create();

    @Inject
    public DrawerFragmentPresenter(@Nonnull @Named("AddNewListClickObservable") final Observable<Void> addNewListClickObservable,
                                   @Nonnull final ListsDao listsDao,
                                   @Nonnull final CurrentListDao currentListDao) {

        listObservable = listsDao.getCurrentListObservable()
                .map(toAdapterItems());

        subscription.set(Subscriptions.from(
                addNewListClickObservable
                        .map(o -> "damian")
                        .flatMap(listsDao::addNewListObservable)
                        .subscribe(),
                setCurrentListSubejct
                        .subscribe(currentListDao.saveCurrentListIdObserver())
        ));
    }

    private Func1<Map<String, String>, List<BaseAdapterItem>> toAdapterItems() {
        return shoppingList -> {
            final List<BaseAdapterItem> output = Lists.newArrayList();

            for (String key : shoppingList.keySet()) {
                output.add(new ShoppingListItem(shoppingList.get(key), key));
            }

            return output;
        };
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getListObservable() {
        return listObservable;
    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

    public class ShoppingListItem implements BaseAdapterItem {

        private final String name;
        private final String key;

        public ShoppingListItem(String name, String key) {
            this.name = name;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
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

        public Observer<Void> clickObserver() {
            return Observers.create(aVoid -> setCurrentListSubejct.onNext(key));
        }
    }

}
