package com.damianmichalak.shopping_list.view


import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.helper.DateHelper
import com.damianmichalak.shopping_list.presenter.ProductsListPresenter
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.list_item_shopping.view.*
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class ProductsListManager @Inject
constructor(private val resources: Resources, private val dateHelper: DateHelper) : ViewHolderManager {

    override fun matches(baseAdapterItem: BaseAdapterItem): Boolean {
        return baseAdapterItem is ProductsListPresenter.ShoppingListItemWithKey
    }

    override fun createViewHolder(parent: ViewGroup, inflater: LayoutInflater): ViewHolderManager.BaseViewHolder<ProductsListPresenter.ShoppingListItemWithKey> {
        return ProductViewHolder(inflater.inflate(R.layout.list_item_shopping, parent, false))
    }

    internal inner class ProductViewHolder(val view: View) : ViewHolderManager.BaseViewHolder<ProductsListPresenter.ShoppingListItemWithKey>(view) {


        //        private final Observable<Void> clickObservable;
        private val subscription = SerialSubscription()


        override fun onViewRecycled() {
            super.onViewRecycled()
//            subscription.set(Subscriptions.empty());
        }

        override fun bind(item: ProductsListPresenter.ShoppingListItemWithKey) {
            val product = item.product
            view.shopping_item_name.text = product.name
            view.shopping_item_date.text = resources.getString(R.string.shopping_list_date_string, dateHelper.getDateForTimestamp(product.dateAdded))

            subscription.set(Subscriptions.from(
                    RxView.clicks(view.shopping_item_done).subscribe(item.removeItem())
            ))
        }

    }

}
