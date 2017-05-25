package com.damianmichalak.shopping_list.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.presenter.AddProductsPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;


public class AddProductsManager implements ViewHolderManager {

    @Inject
    public AddProductsManager() {
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof AddProductsPresenter.SuggestedProductItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new ProductsViewHolder(inflater.inflate(R.layout.list_item_products, parent, false));
    }

    class ProductsViewHolder extends BaseViewHolder {

        @BindView(R.id.product_item_name)
        TextView productName;

        @Nonnull
        private final SerialSubscription subscription = new SerialSubscription();

        public ProductsViewHolder(@Nonnull View itemView) {
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
            final AddProductsPresenter.SuggestedProductItem productItem = (AddProductsPresenter.SuggestedProductItem) item;
            productName.setText(productItem.getProduct());

            subscription.set(Subscriptions.from(
                    RxView.clicks(productName)
                            .subscribe(productItem.clickAction())
            ));

        }
    }

}
