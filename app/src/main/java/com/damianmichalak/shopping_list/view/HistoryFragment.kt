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
import com.damianmichalak.shopping_list.presenter.HistoryPresenter
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter
import com.jakewharton.rxbinding.view.RxView
import dagger.Provides
import kotlinx.android.synthetic.main.fragment_history_list.*
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class HistoryFragment : BaseFragment() {

    @Inject
    lateinit var presenter: HistoryPresenter

    @Inject
    lateinit var manager: HistoryProductsListManager

    private val subscription = SerialSubscription()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_history_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle(getString(R.string.history_toolbar_title))

        history_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        val adapter = RxUniversalAdapter(listOf(manager))
        history_list_recycler_view.adapter = adapter

        subscription.set(Subscriptions.from(
                presenter.historyProductsForCurrentList
                        .subscribe(adapter),
                presenter.listNameObservable
                        .subscribe { (activity as MainActivity).setToolbarSubtitle(it) },
                presenter.historyEmptyObservable
                        .subscribe(RxView.visibility(history_empty_view))
        ))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Subscriptions.empty())
    }

    override fun initDagger() {
        val component = DaggerHistoryFragment_Component
                .builder()
                .module(Module(this))
                .applicationComponent((activity as BaseActivity).applicationComponent)
                .build()

        component.inject(this)

    }

    @FragmentScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class), modules = arrayOf(HistoryFragment.Module::class))
    interface Component {
        fun inject(historyFragment: HistoryFragment)
    }

    @dagger.Module
    inner class Module(private val fragment: Fragment) {

        @Provides
        fun provideLayoutInflater(): LayoutInflater {
            return fragment.activity.layoutInflater
        }

        @Provides
        internal fun provideResources(): Resources {
            return fragment.resources
        }

    }

    companion object {

        val TAG = "HistoryFragment"

        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }
}
