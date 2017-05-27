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
import javax.inject.Named;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.Observers;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ShoppingListPresenter {

    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<Boolean> emptyListObservable;
    @Nonnull
    private final CurrentListDao currentListDao;
    @Nonnull
    private final Observable<List<BaseAdapterItem>> listObservable;
    @Nonnull
    private final Observable<Object> showNewListDialogObservable;
    @Nonnull
    private final PublishSubject<Object> refreshList = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> allowedToRemoveSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> setCurrentListSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> addNewListClickSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> removeListClickSubject = PublishSubject.create();

    @Inject
    ShoppingListPresenter(@Nonnull final CurrentListDao currentListDao,
                          @Nonnull final ListsDao listsDao,
                          @Named("AddListEmptyClickObservable") Observable<Void> addListEmptyClickObservable,
                          @Named("AddListClickObservable") Observable<Void> addListClickObservable) {
        this.currentListDao = currentListDao;

        listObservable = refreshList
                .startWith((Object) null)
                .flatMap(o -> listsDao.getAvailableListsObservable())
                .map(toAdapterItems())
                .replay(1)
                .refCount();

        emptyListObservable = listObservable.map(List::isEmpty);

        showNewListDialogObservable = Observable.merge(addListEmptyClickObservable, addListClickObservable)
                .map(v -> null);

        subscription.set(Subscriptions.from(
                addNewListClickSubject
                        .subscribe(listsDao::addNewListObservable),
                removeListClickSubject
                        .subscribe(listsDao::removeListObservable),
                setCurrentListSubject
                        .subscribe(currentListDao.saveCurrentListIdObserver())
        ));

    }

    @Nonnull
    public Observable<Object> getShowNewListDialogObservable() {
        return showNewListDialogObservable;
    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

    @Nonnull
    public Observer<String> getAddNewListClickSubject() {
        return addNewListClickSubject;
    }

    @Nonnull
    public Observable<Boolean> getEmptyListObservable() {
        return emptyListObservable;
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getListObservable() {
        return listObservable;
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
            return item instanceof ShoppingListPresenter.ShoppingListItem && ((ShoppingListItem) item).key.equals(key);
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

        public Observer<Void> longClickObserver() {
            return Observers.create(aVoid -> allowedToRemoveSubject.onNext(key));
        }

        public Observable<Boolean> removeAllowedObservable() {
            return allowedToRemoveSubject
                    .map(allowedToRemoveId -> allowedToRemoveId.equals(key))
                    .withLatestFrom(isCurrentList(), (isLongClicked, isCurrentList)
                            -> isLongClicked && !isCurrentList);
        }

        public Observable<Boolean> isCurrentList() {
            return currentListDao.getCurrentListKeyObservable()
                    .map(key::equals)
                    .startWith(false);
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
