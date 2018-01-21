package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.damianmichalak.shopping_list.model.UserDao;
import com.damianmichalak.shopping_list.model.UserPreferences;
import com.damianmichalak.shopping_list.model.apiModels.ShoppingList;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class MainActivityPresenter {

    @Nonnull
    private final Observable<Object> closeDrawerObservable;
    @Nonnull
    private final PublishSubject<String> qrCodeShoppingListSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> currentOpenedFragmentSubject = PublishSubject.create();
    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<ShoppingList> qrCodeListError;
    @Nonnull
    private final Observable<Object> showWelcomeScreenObservable;
    @Nonnull
    private final Observable<String> qrCodeListSuccess;
    @Nonnull
    private final Observable<String> currentOpenedFragment;

    @Inject
    public MainActivityPresenter(@Nonnull final CurrentListDao currentListDao,
                                 @Nonnull final ListsDao listsDao,
                                 @Nonnull final UserPreferences userPreferences,
                                 @Nonnull final UserDao userDao) {

        closeDrawerObservable = currentListDao.getCurrentListKeyObservable()
                .map(o -> null);

        subscription.set(Subscriptions.from(
                currentOpenedFragmentSubject.subscribe(userPreferences::setCurrentOpenedFragment)
        ));

        currentOpenedFragment = Observable.fromCallable(userPreferences::getCurrentOpenedFragment);

        qrCodeListSuccess = qrCodeShoppingListSubject
                .flatMap(qrCodeKey -> listsDao.getObservableForSingleList(qrCodeKey)
                        .filter(shoppingList -> shoppingList != null)
                        .flatMap(shoppingList -> listsDao.addNewAvailableListObservable(qrCodeKey, shoppingList.getName())
                                .map(o -> shoppingList.getName())))
                .distinctUntilChanged();

        qrCodeListError = qrCodeShoppingListSubject
                .flatMap(listsDao::getObservableForSingleList)
                .filter(shoppingList -> shoppingList == null);

        showWelcomeScreenObservable = userDao.getUidObservable()
                .filter(o -> o == null)
                .map(o -> null);

    }

    @Nonnull
    public Observable<String> getCurrentOpenedFragment() {
        return currentOpenedFragment;
    }

    @Nonnull
    public Observable<Object> getShowWelcomeScreenObservable() {
        return showWelcomeScreenObservable;
    }

    @Nonnull
    public Observer<String> getCurrentOpenedFragmentSubject() {
        return currentOpenedFragmentSubject;
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
    public Observable<Object> getCloseDrawerObservable() {
        return closeDrawerObservable;
    }
}
