package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.User;
import com.damianmichalak.shopping_list.model.UserDao;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class MainActivityPresenter {

    @Nonnull
    private final UserDao userDao;
    @Nonnull
    private final Observable<Object> closeDrawerObservable;
    @Nonnull
    private final PublishSubject<Object> removeListClickSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> qrCodeShoppingListSubject = PublishSubject.create();
    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<ShoppingList> qrCodeListError;
    @Nonnull
    private final Observable<String> qrCodeListSuccess;
    @Nonnull
    private final Observable<User> emptyUserNameObservable;

    @Inject
    public MainActivityPresenter(@Nonnull final CurrentListDao currentListDao,
                                 @Nonnull final ListsDao listsDao,
                                 @Nonnull final UserDao userDao) {

        closeDrawerObservable = currentListDao.getCurrentListKeyObservable()
                .map(o -> null);
        this.userDao = userDao;

        subscription.set(Subscriptions.from(
                removeListClickSubject
                        .withLatestFrom(currentListDao.getCurrentListKeyObservable(), (o, currentListKey) -> currentListKey)
                        .flatMap(listsDao::removeListObservable)
                        .subscribe()
        ));

        qrCodeListSuccess = qrCodeShoppingListSubject
                .flatMap(qrCodeKey -> listsDao.getObservableForSingleList(qrCodeKey)
                        .filter(shoppingList -> shoppingList != null)
                        .flatMap(shoppingList -> listsDao.addNewAvailableListObservable(qrCodeKey, shoppingList.getName())
                                .map(o -> shoppingList.getName())));

        qrCodeListError = qrCodeShoppingListSubject
                .flatMap(listsDao::getObservableForSingleList)
                .filter(shoppingList -> shoppingList == null);

        emptyUserNameObservable = userDao.getUserObservable()
                .filter(user -> Strings.isNullOrEmpty(user.getName()))
                .first();

    }

    @Nonnull
    public Observable<User> getEmptyUserNameObservable() {
        return emptyUserNameObservable;
    }

    @Nonnull
    public Observer<String> getNewUserNameSubject() {
        return userDao.usernameObserver();
    }

    @Nonnull
    public Observable<String> getQrCodeListSuccess() {
        return qrCodeListSuccess;
    }

    @Nonnull
    public Observable<ShoppingList> getQrCodeListError() {
        return qrCodeListError;
    }

    @Nonnull
    public Observer<String> getQrCodeShoppingListSubject() {
        return qrCodeShoppingListSubject;
    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

    @Nonnull
    public Observer<Object> getRemoveListClickSubject() {
        return removeListClickSubject;
    }

    @Nonnull
    public Observable<Object> getCloseDrawerObservable() {
        return closeDrawerObservable;
    }
}
