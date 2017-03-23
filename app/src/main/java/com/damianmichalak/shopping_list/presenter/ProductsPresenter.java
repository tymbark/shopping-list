package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.damianmichalak.shopping_list.model.UserPreferences;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ProductsPresenter {


    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<List<BaseAdapterItem>> suggestedProductsObservable;

    @Inject
    public ProductsPresenter(@Nonnull @Named("AddItemClickObservable") final Observable<Void> clickObservable,
                             @Nonnull @Named("ProductTextInputObservable") final Observable<CharSequence> textChanges,
                             @Nonnull final ShoppingListDao dao,
                             @Nonnull final UserPreferences userPreferences) {

        suggestedProductsObservable = Observable.fromCallable(new Callable<List<BaseAdapterItem>>() {
            @Override
            public List<BaseAdapterItem> call() throws Exception {
                final Set<String> suggestedProducts = userPreferences.getSuggestedProducts();
                final List<BaseAdapterItem> adapterItems = Lists.newArrayList();

                for (String product : suggestedProducts) {
                    adapterItems.add(new SuggestedProductItem(product));
                }

                return adapterItems;
            }
        });

        subscription.set(Subscriptions.from(
                clickObservable.withLatestFrom(textChanges, new Func2<Void, CharSequence, String>() {
                    @Override
                    public String call(Void aVoid, CharSequence charSequence) {
                        return charSequence.toString();
                    }
                })
                        .flatMap(new Func1<String, Observable<?>>() {
                            @Override
                            public Observable<?> call(String input) {
                                userPreferences.addProductSuggested(input);
                                return dao.addNewItemObservable(input);
                            }
                        })
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
    }

}
