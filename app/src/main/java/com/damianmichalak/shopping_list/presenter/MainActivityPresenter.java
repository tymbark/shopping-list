package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.CurrentListDao;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;

public class MainActivityPresenter {


    @Nonnull
    private final Observable<Object> closeDrawerObservable;

    @Inject
    public MainActivityPresenter(@Nonnull final CurrentListDao currentListDao) {
        closeDrawerObservable = currentListDao.getCurrentListKeyObservable()
                .map(o -> null);
    }

    @Nonnull
    public Observable<Object> getCloseDrawerObservable() {
        return closeDrawerObservable;
    }
}
