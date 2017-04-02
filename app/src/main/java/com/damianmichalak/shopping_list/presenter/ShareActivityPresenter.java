package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.model.CurrentListDao;

import javax.inject.Inject;

import rx.Observable;

public class ShareActivityPresenter {

    private final Observable<String> currentListKeyObservable;

    @Inject
    public ShareActivityPresenter(CurrentListDao currentListDao) {

        currentListKeyObservable = currentListDao.getCurrentListKeyObservable();
    }

    public Observable<String> getCurrentListKeyObservable() {
        return currentListKeyObservable;
    }

}
