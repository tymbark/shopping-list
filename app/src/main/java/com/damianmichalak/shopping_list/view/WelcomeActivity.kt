package com.damianmichalak.shopping_list.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import butterknife.ButterKnife
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.ActivityScope
import com.damianmichalak.shopping_list.helper.AuthHelper
import com.damianmichalak.shopping_list.helper.ConnectivityHelper
import com.damianmichalak.shopping_list.helper.DialogHelper
import com.damianmichalak.shopping_list.presenter.WelcomePresenter
import com.jakewharton.rxbinding.view.RxView
import dagger.Provides
import kotlinx.android.synthetic.main.welcome_activity.*
import rx.Observable
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject
import javax.inject.Named

class WelcomeActivity : BaseActivity() {

    @Inject
    lateinit var presenter: WelcomePresenter
    @Inject
    lateinit var authHelper: AuthHelper

    private val subscription = SerialSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        ButterKnife.bind(this)
        initDagger()
        authHelper.onCreate()

        subscription.set(Subscriptions.from(
                presenter.retryConnectionObservable
                        .subscribe { authHelper.onCreate() },
                presenter.errorNoInternetObservable
                        .subscribe { Snackbar.make(welcome_root_view, R.string.welcome_error_internet, Snackbar.LENGTH_SHORT).show() },
                presenter.errorNotConnectedYetObservable
                        .subscribe { Snackbar.make(welcome_root_view, R.string.welcome_error_not_connected, Snackbar.LENGTH_SHORT).show() },
                presenter.closeActivityObservable
                        .subscribe {
                            startActivity(MainActivity.newIntent(this@WelcomeActivity))
                            finish()
                        },
                presenter.showUserNameDialogObservable
                        .subscribe { DialogHelper.showUserNameInputDialog(this@WelcomeActivity, presenter.newUserNameSubject, null) }
        ))

    }

    public override fun onStart() {
        super.onStart()
        authHelper.onStart(this)
    }

    public override fun onStop() {
        super.onStop()
        authHelper.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.set(Subscriptions.empty())
    }

    override fun initDagger() {
        DaggerWelcomeActivity_Component
                .builder()
                .applicationComponent(applicationComponent)
                .module(Module())
                .build()
                .inject(this)
    }

    @ActivityScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class), modules = arrayOf(Module::class))
    interface Component {

        fun inject(activity: WelcomeActivity)

    }

    @dagger.Module
    internal inner class Module {

        @Provides
        @Named("NextClickWelcomeObservable")
        fun provideNextClickWelcomeObservable(): Observable<Void> {
            return RxView.clicks(welcome_next_button).share()
        }

        @Provides
        fun provideConnectivityHelper(): ConnectivityHelper {
            return ConnectivityHelper(this@WelcomeActivity)
        }

    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }

}
