package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.HistoryDao;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;

public class HistoryPresenter {

    @Nonnull
    private final Observable<List<BaseAdapterItem>> historyProductsForCurrentList;
    @Nonnull
    private final Observable<Boolean> historyEmptyObservable;
    @Nonnull
    private final PublishSubject<Product> removeItemSubject = PublishSubject.create();
    @Nonnull
    private final HistoryDao historyDao;

    @Inject
    public HistoryPresenter(@Nonnull final HistoryDao historyDao) {

        this.historyDao = historyDao;
        historyProductsForCurrentList = historyDao.getProductsObservable()
                .map(toAdapterItems())
                .replay(1)
                .refCount();

        historyEmptyObservable = historyProductsForCurrentList
                .map(List::isEmpty);

    }

    private Func1<Map<String, Product>, List<BaseAdapterItem>> toAdapterItems() {
        return stringProductMap -> {
            final List<BaseAdapterItem> items = Lists.newArrayList();
            for (String key : stringProductMap.keySet()) {
                final Product product = stringProductMap.get(key);
                items.add(new HistoryItem(product));
            }

            return items;
        };
    }

    @Nonnull
    public Observable<Boolean> getHistoryEmptyObservable() {
        return historyEmptyObservable;
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getHistoryProductsForCurrentList() {
        return historyProductsForCurrentList;
    }

    @Nonnull
    public Observer<Product> getRemoveItemSubject() {
        return removeItemSubject;
    }

    public class HistoryItem implements BaseAdapterItem {

        @Nonnull
        private final Product product;

        @Nonnull
        public Product getProduct() {
            return product;
        }

        public HistoryItem(@Nonnull Product product) {
            this.product = product;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HistoryPresenter.HistoryItem)) return false;
            HistoryPresenter.HistoryItem that = (HistoryPresenter.HistoryItem) o;
            return Objects.equal(product, that.product);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(product);
        }

        @Override
        public long adapterId() {
            return 0;
        }

        @Override
        public boolean matches(@Nonnull BaseAdapterItem item) {
            return item instanceof HistoryPresenter.HistoryItem && ((HistoryPresenter.HistoryItem) item).product.getId().equals(product.getId());
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return equals(item);
        }

        public Observer<Object> removeItem() {
            return Observers.create(o -> removeItemSubject.onNext(product));
        }
    }

}
