package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.helper.ConnectivityHelper;
import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.damianmichalak.shopping_list.model.UserDao;
import com.damianmichalak.shopping_list.model.apiModels.User;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.subjects.PublishSubject;

public class WelcomePresenter {

    @Nonnull
    private final Observable<User> showUserNameDialogObservable;
    @Nonnull
    private final Observable<Object> closeActivityObservable;
    @Nonnull
    private final Observable<Object> errorNoInternetObservable;
    @Nonnull
    private final Observable<Object> errorNotConnectedYetObservable;
    @Nonnull
    private final Observable<Object> retryConnectionObservable;
    @Nonnull
    private final PublishSubject<Object> finishActivitySubject = PublishSubject.create();
    @Nonnull
    private final UserDao userDao;

    @Inject
    public WelcomePresenter(
            @Nonnull final ConnectivityHelper connectivityHelper,
            @Nonnull final UserDao userDao,
            @Named("NextClickWelcomeObservable") Observable<Void> nextClickWelcomeObservable,
            @Named("UI") Scheduler uiScheduler) {
        this.userDao = userDao;

        final Observable<String> clickWithUid = nextClickWelcomeObservable
                .withLatestFrom(userDao.getUidObservable(), (click, uid) -> uid)
                .replay(1)
                .refCount();

        final Observable<User> clickWithUser = nextClickWelcomeObservable
                .withLatestFrom(userDao.getUserObservable(), (click, user) -> user)
                .replay(1)
                .refCount();

        errorNoInternetObservable = clickWithUid
                .filter(user -> !connectivityHelper.isNetworkAvailable())
                .map(o -> null);

        errorNotConnectedYetObservable = clickWithUid
                .filter(user -> connectivityHelper.isNetworkAvailable())
                .filter(uid -> uid == null)
                .map(o -> o = null);

        showUserNameDialogObservable = clickWithUser
                .filter(user -> connectivityHelper.isNetworkAvailable())
                .filter(user -> user != null)
                .filter(user -> Strings.isNullOrEmpty(user.getName()))
                .throttleFirst(500, TimeUnit.MILLISECONDS, uiScheduler);

        closeActivityObservable = userDao.getUserObservable()
                .filter(user -> connectivityHelper.isNetworkAvailable())
                .filter(user -> user != null)
                .filter(user -> Strings.isNotNullAndNotEmpty(user.getName()))
                .map(o -> null);

        retryConnectionObservable = clickWithUid
                .filter(user -> connectivityHelper.isNetworkAvailable())
                .filter(uid -> uid == null)
                .map(o -> null);

    }

    @Nonnull
    public Observable<Object> getRetryConnectionObservable() {
        return retryConnectionObservable;
    }

    @Nonnull
    public Observable<Object> getErrorNotConnectedYetObservable() {
        return errorNotConnectedYetObservable;
    }

    @Nonnull
    public Observable<Object> getErrorNoInternetObservable() {
        return errorNoInternetObservable;
    }

    @Nonnull

    public Observable<Object> getCloseActivityObservable() {
        return closeActivityObservable;
    }

    @Nonnull
    public Observer<String> getNewUserNameSubject() {
        return userDao.usernameObserver();
    }

    @Nonnull
    public Observable<User> getShowUserNameDialogObservable() {
        return showUserNameDialogObservable;
    }
}
