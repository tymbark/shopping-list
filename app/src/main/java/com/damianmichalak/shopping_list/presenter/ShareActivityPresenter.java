package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.apiModels.ShoppingList;

import javax.inject.Inject;

import rx.Observable;

public class ShareActivityPresenter {

    private final Observable<String> currentListKeyObservable;
    private final Observable<String> currentListNameObservable;

    @Inject
    public ShareActivityPresenter(CurrentListDao currentListDao) {

        currentListKeyObservable = currentListDao.getCurrentListKeyObservable();
        currentListNameObservable = currentListDao.getCurrentListObservable()
                .filter(list -> list != null)
                .map(ShoppingList::getName);
    }

    public Observable<String> getCurrentListNameObservable() {
        return currentListNameObservable;
    }

    public Observable<String> getCurrentListKeyObservable() {
        return currentListKeyObservable;
    }

}
