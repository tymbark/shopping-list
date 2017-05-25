package com.damianmichalak.shopping_list.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;
import com.jakewharton.rxbinding.view.RxView;

import java.text.DateFormat;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class HistoryProductsListManager implements ViewHolderManager {

    private DateFormat dateFormat = DateFormat.getDateTimeInstance();

    @Inject
    public HistoryProductsListManager() {

    }

    @Override
    public boolean matches(@Nonnull BaseAdapterItem baseAdapterItem) {
        return baseAdapterItem instanceof HistoryPresenter.HistoryItem;
    }

    @Nonnull
    @Override
    public BaseViewHolder createViewHolder(@Nonnull ViewGroup parent, @Nonnull LayoutInflater inflater) {
        return new HistoryViewHolder(inflater.inflate(R.layout.list_item_shopping, parent, false));
    }

    class HistoryViewHolder extends BaseViewHolder {

        @BindView(R.id.shopping_item_name)
        TextView itemName;
        @BindView(R.id.shopping_item_date)
        TextView itemDate;
        @BindView(R.id.shopping_item_done)
        View done;

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
            itemDate.setText(dateFormat.format(new Date(product.getDateAdded())));

            subscription.set(Subscriptions.from(
                    RxView.clicks(done)
                            .subscribe(adapterItem.removeItem())
            ));
        }

    }

}
