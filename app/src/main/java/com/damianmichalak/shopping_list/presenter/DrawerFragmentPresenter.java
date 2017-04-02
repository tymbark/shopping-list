package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
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
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class DrawerFragmentPresenter {

    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<List<BaseAdapterItem>> listObservable;
    @Nonnull
    private final PublishSubject<String> setCurrentListSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> addNewListClickSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> removeListClickSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> refreshList = PublishSubject.create();

    @Inject
    public DrawerFragmentPresenter(@Nonnull final ListsDao listsDao,
                                   @Nonnull final CurrentListDao currentListDao) {

        listObservable = refreshList
                .startWith((Object) null)
                .flatMap(o -> listsDao.getAvailableListsObservable().map(toAdapterItems()));

        subscription.set(Subscriptions.from(
                addNewListClickSubject
                        .flatMap(listsDao::addNewListObservable)
                        .subscribe(),
                removeListClickSubject
                        .flatMap(listsDao::removeListObservable)
                        .subscribe(),
                setCurrentListSubject
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
    public Observer<String> getAddNewListClickSubject() {
        return addNewListClickSubject;
    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

    public Observer<Object> refreshList() {
        return refreshList;
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
            return item instanceof ShoppingListItem;
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return false;
        }

        public Observer<Void> clickObserver() {
            return Observers.create(aVoid -> setCurrentListSubject.onNext(key));
        }

        public Observer<Void> removeObserver() {
            return Observers.create(aVoid -> removeListClickSubject.onNext(key));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShoppingListItem)) return false;
            ShoppingListItem that = (ShoppingListItem) o;
            return Objects.equal(name, that.name) &&
                    Objects.equal(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, key);
        }
    }

}
