package com.damianmichalak.shopping_list.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.model.ShoppingItem;
import com.damianmichalak.shopping_list.presenter.ShoppingListPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListManager implements ViewHolderManager {

    @Inject
    public ShoppingListManager() {
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof ShoppingListPresenter.ShoppingListItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new ShoppingViewHolder(inflater.inflate(R.layout.shopping_item, parent, false));
    }

    class ShoppingViewHolder extends BaseViewHolder {

        @BindView(R.id.shopping_item_name)
        TextView itemName;

        @BindView(R.id.shopping_item_description)
        TextView itemDesc;

        public ShoppingViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(@Nonnull BaseAdapterItem item) {
            final ShoppingItem shoppingItem = ((ShoppingListPresenter.ShoppingListItem) item).getShoppingItem();
            itemName.setText(shoppingItem.getName());
            itemDesc.setText(shoppingItem.getDescription());
        }

    }

}
