package com.damianmichalak.shopping_list.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.presenter.ProductsPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductsListManager implements ViewHolderManager {

    @Inject
    public ProductsListManager() {
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof ProductsPresenter.SuggestedProductItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new ProductsViewHolder(inflater.inflate(R.layout.list_item_products, parent, false));
    }

    class ProductsViewHolder extends BaseViewHolder {

        @BindView(R.id.product_item_name)
        TextView productName;

        public ProductsViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(@Nonnull BaseAdapterItem item) {
            final ProductsPresenter.SuggestedProductItem productItem = (ProductsPresenter.SuggestedProductItem) item;
            productName.setText(productItem.getProduct());
        }
    }

}
