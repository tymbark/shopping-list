package com.damianmichalak.shopping_list.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;
import com.damianmichalak.shopping_list.helper.AuthHelper;
import com.damianmichalak.shopping_list.helper.ConnectivityHelper;
import com.damianmichalak.shopping_list.helper.DialogHelper;
import com.damianmichalak.shopping_list.presenter.WelcomePresenter;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Provides;
import rx.Observable;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.welcome_root_view)
    View rootView;
    @BindView(R.id.welcome_next_button)
    View nextButton;

    @Inject
    WelcomePresenter presenter;
    @Inject
    AuthHelper authHelper;

    private final SerialSubscription subscription = new SerialSubscription();

    @Nonnull
    public static Intent newIntent(Context context) {
        return new Intent(context, WelcomeActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        ButterKnife.bind(this);
        initDagger();
        authHelper.onCreate();

        subscription.set(Subscriptions.from(
                presenter.getRetryConnectionObservable()
                        .subscribe(o -> authHelper.onCreate()),
                presenter.getErrorNoInternetObservable()
                        .subscribe(o -> Snackbar.make(rootView, R.string.welcome_error_internet, Snackbar.LENGTH_SHORT).show()),
                presenter.getErrorNotConnectedYetObservable()
                        .subscribe(o -> Snackbar.make(rootView, R.string.welcome_error_not_connected, Snackbar.LENGTH_SHORT).show()),
                presenter.getCloseActivityObservable()
                        .subscribe(o -> {
                            startActivity(MainActivity.newIntent(WelcomeActivity.this));
                            finish();
                        }),
                presenter.getShowUserNameDialogObservable()
                        .subscribe(o -> DialogHelper.showUserNameInputDialog(WelcomeActivity.this, presenter.getNewUserNameSubject(), null))
        ));

    }

    @Override
    public void onStart() {
        super.onStart();
        authHelper.onStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        authHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void initDagger() {
        DaggerWelcomeActivity_Component
                .builder()
                .applicationComponent(getApplicationComponent())
                .module(new Module())
                .build()
                .inject(this);
    }

    @ActivityScope
    @dagger.Component(
            dependencies = MainApplication.ApplicationComponent.class,
            modules = Module.class
    )
    public interface Component {

        void inject(@Nonnull final WelcomeActivity activity);

    }

    @dagger.Module
    class Module {

        @Nonnull
        @Provides
        @Named("NextClickWelcomeObservable")
        Observable<Void> provideNextClickWelcomeObservable() {
            return RxView.clicks(nextButton).share();
        }

        @Provides
        @Nonnull
        ConnectivityHelper provideConnectivityHelper() {
            return new ConnectivityHelper(WelcomeActivity.this);
        }

    }

}
