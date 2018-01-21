package com.damianmichalak.shopping_list.view;


import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.helper.DateHelper;
import com.damianmichalak.shopping_list.model.apiModels.Product;
import com.damianmichalak.shopping_list.presenter.HistoryPresenter;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class HistoryProductsListManager implements ViewHolderManager {

    @Nonnull
    private final Resources resources;
    @Nonnull
    private final DateHelper dateHelper;

    @Inject
    public HistoryProductsListManager(@Nonnull Resources resources, @Nonnull DateHelper dateHelper) {
        this.resources = resources;
        this.dateHelper = dateHelper;
    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof HistoryPresenter.HistoryItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new HistoryViewHolder(inflater.inflate(R.layout.history_list_item, parent, false));
    }

    class HistoryViewHolder extends BaseViewHolder {

        @BindView(R.id.history_item_name)
        TextView itemName;
        @BindView(R.id.history_item_date)
        TextView itemDate;

        @Nonnull
        private final SerialSubscription subscription = new SerialSubscription();

        public HistoryViewHolder(@Nonnull View itemView) {
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
            final HistoryPresenter.HistoryItem adapterItem = (HistoryPresenter.HistoryItem) item;
            final Product product = adapterItem.getProduct();
            itemName.setText(product.getName());
            itemDate.setText(resources.getString(R.string.history_date_string, dateHelper.getDateForTimestamp(product.getDatePurchased())));

            subscription.set(Subscriptions.from(
            ));
        }

    }

}
