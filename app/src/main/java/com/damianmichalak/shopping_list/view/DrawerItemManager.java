package com.damianmichalak.shopping_list.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.presenter.DrawerFragmentPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;


public class DrawerItemManager implements ViewHolderManager {

    @Inject
    public DrawerItemManager() {
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof DrawerFragmentPresenter.ShoppingListItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new ViewHolder(inflater.inflate(R.layout.drawer_list_item, parent, false));
    }

    class ViewHolder extends BaseViewHolder {

        @Nonnull
        private final TextView text;
        @Nonnull
        private final View remove;
        @Nonnull
        private final SerialSubscription subscription = new SerialSubscription();

        public ViewHolder(@Nonnull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.drawer_item_text);
            remove = itemView.findViewById(R.id.drawer_item_remove);
        }

        @Override
        public void onViewRecycled() {
            super.onViewRecycled();
            subscription.set(Subscriptions.empty());
        }

        @Override
        public void bind(@Nonnull BaseAdapterItem item) {
            final DrawerFragmentPresenter.ShoppingListItem adapterItem = (DrawerFragmentPresenter.ShoppingListItem) item;
            text.setText(adapterItem.getName());
            remove.setVisibility(View.GONE);

            subscription.set(Subscriptions.from(
                    RxView.clicks(text)
                            .subscribe(adapterItem.clickObserver()),
                    RxView.clicks(remove)
                            .subscribe(adapterItem.removeObserver()),
                    RxView.longClicks(text)
                            .subscribe(aVoid -> remove.setVisibility(View.VISIBLE))
            ));

        }
    }

}
