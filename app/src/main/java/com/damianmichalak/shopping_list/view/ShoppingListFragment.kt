package com.damianmichalak.shopping_list.view


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.FragmentScope
import com.damianmichalak.shopping_list.helper.DialogHelper
import com.damianmichalak.shopping_list.presenter.ShoppingListPresenter
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter
import com.jakewharton.rxbinding.view.RxView
import dagger.Provides
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import rx.Observable
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject
import javax.inject.Named

class ShoppingListFragment : BaseFragment() {

    @Inject
    lateinit var presenter: ShoppingListPresenter
    @Inject
    lateinit var manager: ShoppingListItemManager

    private val subscription = SerialSubscription()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle(getString(R.string.shopping_list_toolbar_title))

        shopping_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        val adapter = RxUniversalAdapter(listOf(manager))
        shopping_list_recycler_view.adapter = adapter

        subscription.set(Subscriptions.from(
                presenter.listObservable
                        .subscribe(adapter),
                presenter.emptyListObservable
                        .subscribe(RxView.visibility(shopping_list_empty_products_view)),
                presenter.showProductsObservable
                        .subscribe {
                            (activity as MainActivity)
                                    .selectItemNavigation(R.id.navigation_products)
                        },
                //                presenter.getFloatingActionButtonObservable()
                //                        .subscribe(RxView.visibility(floatingActionButtonAdd)),
                presenter.showNewListDialogObservable
                        .subscribe { DialogHelper.showNewListNameDialog(activity, presenter.addNewListClickSubject) },
                presenter.listNameObservable
                        .subscribe { (activity as MainActivity).setToolbarSubtitle(it) },
                presenter.subscription
        ))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Subscriptions.empty())
    }

    override fun initDagger() {
        val component = DaggerShoppingListFragment_Component
                .builder()
                .module(Module(this))
                .applicationComponent((activity as BaseActivity).applicationComponent)
                .build()

        component.inject(this)
    }

    @FragmentScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class), modules = arrayOf(Module::class))
    interface Component {

        fun inject(shoppingListFragment: ShoppingListFragment)
    }

    @dagger.Module
    inner class Module(private val fragment: Fragment) {

        @Provides
        fun provideLayoutInflater(): LayoutInflater {
            return fragment.activity.layoutInflater
        }

        @Provides
        @Named("AddListEmptyClickObservable")
        internal fun provideAddListEmptyClickObservable(): Observable<Void> {
            return RxView.clicks(shopping_list_empty_products_view).share()
        }

        @Provides
        @Named("AddListClickObservable")
        internal fun provideAddListClickObservable(): Observable<Void> {
            return RxView.clicks(shopping_list_add_button).share()
        }

    }

    companion object {

        val TAG = "ShoppingListFragment"

        fun newInstance(): ShoppingListFragment {
            return ShoppingListFragment()
        }
    }

}
