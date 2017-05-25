package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.StringResources;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.damianmichalak.shopping_list.model.UserDao;
import com.damianmichalak.shopping_list.model.api_models.User;
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
    private final UserDao userDao;
    @Nonnull
    private final Observable<String> showChangeUsernameDialogObservable;
    @Nonnull
    private final Observable<String> usernameObservable;
    @Nonnull
    private final Observable<String> currentListNameObservable;

    @Inject
    public DrawerFragmentPresenter(@Nonnull final ListsDao listsDao,
                                   @Nonnull final CurrentListDao currentListDao,
                                   @Nonnull final UserDao userDao,
                                   @Nonnull StringResources stringResources,
                                   @Nonnull @Named("changeUsernameClickObservable") final Observable<Void> changeUsernameClickObservable) {
        this.userDao = userDao;

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
                })
                .startWith(stringResources.getString(R.string.drawer_current_list_empty));

        showChangeUsernameDialogObservable = changeUsernameClickObservable
                .withLatestFrom(usernameObservable, (v, username) -> username);

        subscription.set(Subscriptions.from(
                addNewListClickSubject
                        .flatMap(listsDao::addNewListObservable)
                        .subscribe(),
                setCurrentListSubject
                        .subscribe(currentListDao.saveCurrentListIdObserver())
        ));
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
    public Observer<String> getAddNewListClickSubject() {
        return addNewListClickSubject;
    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

}
