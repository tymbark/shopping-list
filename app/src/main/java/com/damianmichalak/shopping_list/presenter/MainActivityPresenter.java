package com.damianmichalak.shopping_list.presenter;


import javax.inject.Inject;

import rx.subjects.PublishSubject;

public class MainActivityPresenter {

    final PublishSubject<String> uidSubject = PublishSubject.create();

    @Inject
    public MainActivityPresenter() {
    }

    public PublishSubject<String> getUidSubject() {
        return uidSubject;
    }
}
