package com.damianmichalak.shopping_list.view


import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.helper.DateHelper
import com.damianmichalak.shopping_list.presenter.HistoryPresenter
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import kotlinx.android.synthetic.main.history_list_item.view.*
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class HistoryProductsListManager @Inject
constructor(private val resources: Resources, private val dateHelper: DateHelper) : ViewHolderManager {

    override fun matches(baseAdapterItem: BaseAdapterItem): Boolean {
        return baseAdapterItem is HistoryPresenter.HistoryItem
    }

    override fun createViewHolder(parent: ViewGroup, inflater: LayoutInflater): ViewHolderManager.BaseViewHolder<HistoryPresenter.HistoryItem> {
        return HistoryViewHolder(inflater.inflate(R.layout.history_list_item, parent, false))
    }

    internal inner class HistoryViewHolder(val view: View) : ViewHolderManager.BaseViewHolder<HistoryPresenter.HistoryItem>(view) {

        private val subscription = SerialSubscription()

        override fun onViewRecycled() {
            super.onViewRecycled()
            subscription.set(Subscriptions.empty())
        }

        override fun bind(item: HistoryPresenter.HistoryItem) {
            val product = item.product
            view.history_item_name.text = product.name
            view.history_item_date.text = resources.getString(R.string.history_date_string, dateHelper.getDateForTimestamp(product.datePurchased))

            subscription.set(Subscriptions.from(
            ))
        }

    }

}
