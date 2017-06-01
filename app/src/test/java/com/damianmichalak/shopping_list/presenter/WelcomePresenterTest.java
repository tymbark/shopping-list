package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.Mocks;
import com.damianmichalak.shopping_list.helper.ConnectivityHelper;
import com.damianmichalak.shopping_list.model.UserDao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

public class WelcomePresenterTest {


    @Mock
    ConnectivityHelper connectivityHelper;
    @Mock
    UserDao userDao;
    private Scheduler scheduler = Schedulers.immediate();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWhenNotClicked_dontDoAnything() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just("uid"));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber1 = new TestSubscriber<>();
        final TestSubscriber<Object> subscriber2 = new TestSubscriber<>();
        final TestSubscriber<Object> subscriber3 = new TestSubscriber<>();
        final TestSubscriber<Object> subscriber4 = new TestSubscriber<>();
        final TestSubscriber<Object> subscriber5 = new TestSubscriber<>();
        presenter.getErrorNoInternetObservable().subscribe(subscriber1);
        presenter.getCloseActivityObservable().subscribe(subscriber2);
        presenter.getShowUserNameDialogObservable().subscribe(subscriber3);
        presenter.getRetryConnectionObservable().subscribe(subscriber4);
        presenter.getErrorNotConnectedYetObservable().subscribe(subscriber5);

        assert_().that(subscriber1.getOnNextEvents()).hasSize(0);
        assert_().that(subscriber2.getOnNextEvents()).hasSize(0);
        assert_().that(subscriber3.getOnNextEvents()).hasSize(0);
        assert_().that(subscriber4.getOnNextEvents()).hasSize(0);
        assert_().that(subscriber5.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withoutInternet_showNoInternetError() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just("uid"));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getErrorNoInternetObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenClicked_withInternet_dontShowNoInternetError() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just("uid"));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(true);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getErrorNoInternetObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withoutInternet_andNotYetConnected_dontShowNotYetError() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getErrorNotConnectedYetObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withInternet_andNotYetConnected_showNotYetError() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(true);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getErrorNotConnectedYetObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenClicked_withInternet_andUserEmpty_showUserNameDialog() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.userEmpty()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(true);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getShowUserNameDialogObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenClicked_withoutInternet_andUserEmpty_dontShowUserNameDialog() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.userEmpty()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getShowUserNameDialogObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withoutInternet_andUserNotEmpty_dontShowUserNameDialog() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getShowUserNameDialogObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenNotClicked_withInternet_andUserNotEmpty_closeActivity() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(true);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getCloseActivityObservable().subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenClicked_withInternet_andUserNotEmpty_closeActivity() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(true);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getCloseActivityObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenClicked_withoutInternet_andUserNotEmpty_dontCloseActivity() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getCloseActivityObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withoutInternet_andNotConnectedYet_dontRetryConnection() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getRetryConnectionObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withoutInternet_andAlreadyConnected_dontRetryConnection() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just("uid"));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(false);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getRetryConnectionObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testWhenClicked_withInternet_andNotConnectedYet_retryConnection() throws Exception {
        final PublishSubject<Void> click = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(Observable.just(null));
        when(userDao.getUserObservable()).thenReturn(Observable.just(Mocks.user()));
        when(connectivityHelper.isNetworkAvailable()).thenReturn(true);
        final WelcomePresenter presenter = new WelcomePresenter(connectivityHelper, userDao, click, scheduler);

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        presenter.getRetryConnectionObservable().subscribe(subscriber);

        click.onNext(null);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

}
