package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.ProductsDao;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.damianmichalak.shopping_list.model.UserPreferences;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Observer;
import rx.observers.Observers;
import rx.subjects.PublishSubject;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ProductsPresenter {


    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<List<BaseAdapterItem>> suggestedProductsObservable;
    @Nonnull
    private final PublishSubject<String> clickItemSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> refreshSubject = PublishSubject.create();

    @Inject
    public ProductsPresenter(@Nonnull @Named("AddItemClickObservable") final Observable<Void> clickObservable,
                             @Nonnull @Named("ProductTextInputObservable") final Observable<CharSequence> textChanges,
                             @Nonnull final ProductsDao dao,
                             @Nonnull final UserPreferences userPreferences) {

        final Observable<List<BaseAdapterItem>> itemsObservable = Observable.fromCallable(() -> {
            final Set<String> suggestedProducts = userPreferences.getSuggestedProducts();
            final List<BaseAdapterItem> adapterItems = Lists.newArrayList();

            for (String product : suggestedProducts) {
                adapterItems.add(new SuggestedProductItem(product));
            }

            return adapterItems;
        });

        suggestedProductsObservable = refreshSubject.startWith((Object) null).concatMap(o -> itemsObservable);

        subscription.set(Subscriptions.from(
                clickObservable.withLatestFrom(textChanges, (aVoid, charSequence) -> charSequence.toString())
                        .flatMap(input -> {
                            userPreferences.addProductSuggested(input);
                            return dao.addNewItemObservable(input);
                        })
                        .doOnNext(s -> refreshSubject.onNext(null))
                        .subscribe(),
                clickItemSubject
                        .doOnNext(userPreferences::removeSuggestedProduct)
                        .doOnNext(s -> refreshSubject.onNext(null))
                        .subscribe()
        ));

    }

    @Nonnull
    public SerialSubscription getSubscription() {
        return subscription;
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getSuggestedProductsObservable() {
        return suggestedProductsObservable;
    }

    public class SuggestedProductItem implements BaseAdapterItem {

        @Nonnull
        private final String product;

        public SuggestedProductItem(@Nonnull String product) {
            this.product = product;
        }

        @Nonnull
        public String getProduct() {
            return product;
        }

        @Override
        public long adapterId() {
            return 0;
        }

        @Override
        public boolean matches(@Nonnull BaseAdapterItem item) {
            return equals(item);
        }

        @Override
        public boolean same(@Nonnull BaseAdapterItem item) {
            return equals(item);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SuggestedProductItem)) return false;
            SuggestedProductItem that = (SuggestedProductItem) o;
            return Objects.equal(product, that.product);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(product);
        }

        public Observer<Void> clickAction() {
            return Observers.create(o -> clickItemSubject.onNext(product));
        }
    }

}
