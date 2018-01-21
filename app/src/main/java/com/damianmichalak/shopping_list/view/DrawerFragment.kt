package com.damianmichalak.shopping_list.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.FragmentScope
import com.damianmichalak.shopping_list.dagger.StringResources
import com.damianmichalak.shopping_list.helper.DialogHelper
import com.damianmichalak.shopping_list.presenter.DrawerFragmentPresenter
import com.google.zxing.integration.android.IntentIntegrator
import com.jakewharton.rxbinding.view.RxView
import dagger.Provides
import kotlinx.android.synthetic.main.fragment_drawer_layout.*
import rx.Observable
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject
import javax.inject.Named

class DrawerFragment : BaseFragment() {

    @Inject
    lateinit var presenter: DrawerFragmentPresenter

    private val subscription = SerialSubscription()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_drawer_layout, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscription.set(Subscriptions.from(
                presenter.currentListNameObservable
                        .subscribe { drawer_current_list_name.setText(it) },
                presenter.usernameObservable
                        .subscribe { drawer_username.setText(it) },
                presenter.subscription,
                presenter.showChangeUsernameDialogObservable
                        .subscribe { oldUsername -> DialogHelper.showUserNameInputDialog(context, presenter.newUsernameObserver(), oldUsername) },
                RxView.clicks(drawer_add_new_list)
                        .subscribe { DialogHelper.showNewListNameDialog(activity, presenter.addNewListClickSubject) },
                RxView.clicks(drawer_scan_qr)
                        .subscribe { scanQR() }
        ))

    }

    private fun scanQR() {
        IntentIntegrator(activity)
                .setBeepEnabled(false)
                .setPrompt(getString(R.string.scanner_label))
                .initiateScan()
        // result is handled in MainActivity class -> onActivityResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Subscriptions.empty())
    }

    override fun initDagger() {
        DaggerDrawerFragment_DrawerFragmentComponent
                .builder()
                .applicationComponent((activity as BaseActivity).applicationComponent)
                .drawerFragmentModule(DrawerFragmentModule())
                .build()
                .inject(this)
    }

    @FragmentScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class), modules = arrayOf(DrawerFragmentModule::class))
    internal interface DrawerFragmentComponent {
        fun inject(drawer: DrawerFragment)
    }

    @dagger.Module
    internal inner class DrawerFragmentModule {

        @Provides
        @Named("changeUsernameClickObservable")
        fun changeUsernameClickObservable(): Observable<Void> {
            return RxView.clicks(drawer_change_username)
        }

        @Provides
        fun provideResources(): StringResources {
            return StringResources { id -> resources.getString(id) }
        }

    }

}
