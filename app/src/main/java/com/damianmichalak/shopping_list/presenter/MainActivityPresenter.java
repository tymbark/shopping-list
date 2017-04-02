package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class MainActivityPresenter {

    @Nonnull
    private final Observable<Object> closeDrawerObservable;
    @Nonnull
    private final PublishSubject<Object> removeListClickSubject = PublishSubject.create();
    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();

    @Inject
    public MainActivityPresenter(@Nonnull final CurrentListDao currentListDao,
                                 @Nonnull final ListsDao listsDao) {
        closeDrawerObservable = currentListDao.getCurrentListKeyObservable()
                .map(o -> null);

        subscription.set(Subscriptions.from(
                removeListClickSubject
                        .withLatestFrom(currentListDao.getCurrentListKeyObservable(), (o, currentListKey) -> currentListKey)
                        .flatMap(listsDao::removeListObservable)
                        .subscribe()
        ));

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
