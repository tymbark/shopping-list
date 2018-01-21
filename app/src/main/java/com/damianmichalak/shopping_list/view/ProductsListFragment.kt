package com.damianmichalak.shopping_list.view


import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.FragmentScope
import com.damianmichalak.shopping_list.helper.DialogHelper
import com.damianmichalak.shopping_list.presenter.ProductsListPresenter
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter
import com.jakewharton.rxbinding.view.RxView
import dagger.Provides
import kotlinx.android.synthetic.main.fragment_products_list.*
import rx.Observable
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject
import javax.inject.Named

class ProductsListFragment : BaseFragment() {
    @Inject
    lateinit var presenter: ProductsListPresenter
    @Inject
    lateinit var manager: ProductsListManager

    private val subscription = SerialSubscription()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_products_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle(getString(R.string.products_toolbar_title))

        products_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        val adapter = RxUniversalAdapter(listOf(manager))
        products_list_recycler_view.adapter = adapter

        subscription.set(Subscriptions.from(
                presenter.currentShoppingListItemsObservable
                        .subscribe(adapter),
                presenter.listNameObservable
                        .subscribe { (activity as MainActivity).setToolbarSubtitle(it) },
                presenter.emptyListObservable
                        .subscribe(RxView.visibility(products_list_empty_products_view)),
                presenter.floatingActionButtonObservable
                        .subscribe(RxView.visibility(products_list_add_button)),
                presenter.noListsObservable
                        .subscribe(RxView.visibility(products_list_empty_view)),
                presenter.showNewListDialogObservable
                        .subscribe { o -> DialogHelper.showNewListNameDialog(activity, presenter.newShoppingListObserver) },
                presenter.subscription
        ))

        products_list_add_button.setOnClickListener { v -> startActivity(AddProductsActivity.newIntent(activity)) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Subscriptions.empty())
    }

    override fun initDagger() {
        val component = DaggerProductsListFragment_Component
                .builder()
                .module(Module(this))
                .applicationComponent((activity as BaseActivity).applicationComponent)
                .build()

        component.inject(this)
    }

    @FragmentScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class), modules = arrayOf(Module::class))
    interface Component {

        fun inject(productsListFragment: ProductsListFragment)
    }

    @dagger.Module
    inner class Module(private val fragment: Fragment) {

        @Provides
        fun provideLayoutInflater(): LayoutInflater {
            return fragment.activity.layoutInflater
        }

        @Provides
        @Named("AddListClickObservable")
        internal fun provideAddListClickObservable(): Observable<Void> {
            return RxView.clicks(products_list_empty_view).share()
        }

        @Provides
        internal fun provideResources(): Resources {
            return fragment.resources
        }

    }

    companion object {

        val TAG = "ProductsListFragment"

        fun newInstance(): ProductsListFragment {
            return ProductsListFragment()
        }
    }

}
