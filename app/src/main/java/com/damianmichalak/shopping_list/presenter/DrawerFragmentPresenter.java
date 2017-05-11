package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.StringResources;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.damianmichalak.shopping_list.model.User;
import com.damianmichalak.shopping_list.model.UserDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

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
    private final PublishSubject<String> setCurrentListSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> addNewListClickSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> removeListClickSubject = PublishSubject.create();
    @Nonnull
    private final UserDao userDao;
    @Nonnull
    private final Observable<String> showChangeUsernameDialogObservable;
    @Nonnull
    private final Observable<String> usernameObservable;
    @Nonnull
    private final Observable<String> currentListNameObservable;
    @Nonnull
    private final Observable<List<BaseAdapterItem>> listObservable;

    @Inject
    public DrawerFragmentPresenter(@Nonnull final ListsDao listsDao,
                                   @Nonnull final CurrentListDao currentListDao,
                                   @Nonnull final UserDao userDao,
                                   @Nonnull StringResources stringResources,
                                   @Nonnull @Named("changeUsernameClickObservable") final Observable<Void> changeUsernameClickObservable) {
        this.userDao = userDao;

        listObservable = listsDao.getAvailableListsObservable()
                .map(toAdapterItems());

        usernameObservable = userDao
                .getUserObservable()
                .filter(user -> user != null)
                .map(User::getName)
                .filter(Strings::isNotNullAndNotEmpty);

        currentListNameObservable = currentListDao
                .getCurrentListObservable()
                .map(shoppingList -> {
                    if (shoppingList != null) {
                        return shoppingList.getName();
                    } else {
                        return stringResources.getString(R.string.drawer_current_list_empty);
                    }
                });

        showChangeUsernameDialogObservable = changeUsernameClickObservable
                .withLatestFrom(usernameObservable, (v, username) -> username);

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
    public Observable<String> getCurrentListNameObservable() {
        return currentListNameObservable;
    }

    @Nonnull
    public Observer<String> newUsernameObserver() {
        return userDao.usernameObserver();
    }

    @Nonnull
    public Observable<String> getShowChangeUsernameDialogObservable() {
        return showChangeUsernameDialogObservable;
    }

    @Nonnull
    public Observable<String> getUsernameObservable() {
        return usernameObservable;
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
