package com.damianmichalak.shopping_list.view;

import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class HistoryPresenter {

//    @Nonnull
//    private final Observable<List<BaseAdapterItem>> historyProductsForCurrentList;
    @Nonnull
    private final PublishSubject<String> removeItemSubject = PublishSubject.create();

    @Inject
    public HistoryPresenter() {

    }

}
