package com.damianmichalak.shopping_list.view


import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.ActivityScope
import com.damianmichalak.shopping_list.helper.RxSnackbar
import com.damianmichalak.shopping_list.helper.guava.Lists
import com.damianmichalak.shopping_list.presenter.AddProductsPresenter
import com.google.android.flexbox.FlexboxLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import dagger.Provides
import kotlinx.android.synthetic.main.activity_product.*
import rx.Observable
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject
import javax.inject.Named

class AddProductsActivity : BaseActivity() {

    @Inject
    lateinit var presenter: AddProductsPresenter
    @Inject
    lateinit var manager: AddProductsManager

    private val subscription = SerialSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        initDagger()

        activity_product_recycler_view.layoutManager = FlexboxLayoutManager()
        val adapter = RxUniversalAdapter(Lists.newArrayList<ViewHolderManager>(manager))
        activity_product_recycler_view.adapter = adapter

        subscription.set(Subscriptions.from(
                presenter.subscription,
                presenter.suggestedProductsObservable.subscribe(adapter),
                presenter.addedItemForSnackbarObservable.subscribe(RxSnackbar.showSnackbar(activity_product_root_view)),
                presenter.closeActivityObservable().subscribe { finish() },
                presenter.clearInputObservable().subscribe { activity_product_input.setText(it) }
        ))

        val supportActionBar = supportActionBar
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar.setTitle(R.string.products_activity_title)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.unsubscribe()
    }

    override fun initDagger() {
        val component = DaggerAddProductsActivity_Component.builder()
                .applicationComponent(applicationComponent)
                .module(Module(this))
                .build()

        component.inject(this)
    }

    @ActivityScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(MainApplication.ApplicationComponent::class))
    interface Component {

        fun inject(activity: AddProductsActivity)

    }

    @dagger.Module
    internal inner class Module(private val addProductsActivity: AddProductsActivity) {

        @Provides
        @Named("AddClickObservable")
        fun provideAddClickObservable(): Observable<Void> {
            return RxView.clicks(activity_product_add).share()
        }

        @Provides
        @Named("DoneClickObservable")
        fun provideDoneClickObservable(): Observable<Void> {
            return RxView.clicks(activity_product_done).share()
        }

        @Provides
        @Named("ProductTextInputObservable")
        fun provideProductTextInputObservable(): Observable<CharSequence> {
            return RxTextView.textChanges(activity_product_input)
        }


    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, AddProductsActivity::class.java)
        }
    }

}
