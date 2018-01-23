package com.damianmichalak.shopping_list.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.presenter.AddProductsPresenter
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.list_item_products.view.*
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject


class AddProductsManager @Inject
constructor() : ViewHolderManager {

    override fun matches(baseAdapterItem: BaseAdapterItem): Boolean {
        return baseAdapterItem is AddProductsPresenter.SuggestedProductItem
    }

    override fun createViewHolder(parent: ViewGroup, inflater: LayoutInflater): ViewHolderManager.BaseViewHolder<AddProductsPresenter.SuggestedProductItem> {
        return ProductsViewHolder(inflater.inflate(R.layout.list_item_products, parent, false))
    }

    internal inner class ProductsViewHolder(val view: View) : ViewHolderManager.BaseViewHolder<AddProductsPresenter.SuggestedProductItem>(view) {

        private val subscription = SerialSubscription()

        override fun onViewRecycled() {
            super.onViewRecycled()
            subscription.set(Subscriptions.empty())
        }

        override fun bind(item: AddProductsPresenter.SuggestedProductItem) {
            view.product_item_name.text = item.product

            subscription.set(Subscriptions.from(
                    RxView.clicks(view.product_item_name)
                            .subscribe(item.clickAction())
            ))

        }
    }

}
