package com.damianmichalak.shopping_list.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.presenter.ProductsListPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class ShoppingListManager implements ViewHolderManager {

    @Inject
    public ShoppingListManager() {
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof ProductsListPresenter.ShoppingListItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new ShoppingViewHolder(inflater.inflate(R.layout.list_item_shopping, parent, false));
    }

    class ShoppingViewHolder extends BaseViewHolder {

        @BindView(R.id.shopping_item_name)
        TextView itemName;

        public ShoppingViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(@Nonnull BaseAdapterItem item) {
            final String product = ((ProductsListPresenter.ShoppingListItem) item).getProduct();
            itemName.setText(product);
        }

    }

}
