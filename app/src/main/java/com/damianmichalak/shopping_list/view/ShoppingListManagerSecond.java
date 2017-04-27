package com.damianmichalak.shopping_list.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.presenter.ProductsListPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ShoppingListManagerSecond implements ViewHolderManager {

    @Inject
    public ShoppingListManagerSecond() {
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof ProductsListPresenter.ShoppingListItemWithKey;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new ShoppingViewHolder(inflater.inflate(R.layout.list_item_shopping, parent, false));
    }

    class ShoppingViewHolder extends BaseViewHolder {

        @BindView(R.id.shopping_item_name)
        TextView itemName;
        @BindView(R.id.shopping_item_done)
        View done;

        @Nonnull
        private final SerialSubscription subscription = new SerialSubscription();

        public ShoppingViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onViewRecycled() {
            super.onViewRecycled();
            subscription.set(Subscriptions.empty());
        }

        @Override
        public void bind(@Nonnull BaseAdapterItem item) {
            final ProductsListPresenter.ShoppingListItemWithKey adapterItem = (ProductsListPresenter.ShoppingListItemWithKey) item;
            final String product = adapterItem.getName();
            itemName.setText(product);

            subscription.set(Subscriptions.from(
                    RxView.clicks(done)
                            .subscribe(adapterItem.removeItem())
            ));
        }

    }

}
