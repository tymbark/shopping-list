package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.damianmichalak.shopping_list.model.ProductsDao;
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

public class AddProductsPresenter {


    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();
    @Nonnull
    private final Observable<List<BaseAdapterItem>> suggestedProductsObservable;
    @Nonnull
    private final PublishSubject<String> clickItemSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> refreshSubject = PublishSubject.create();
    @Nonnull
    private final Observable<String> addedItemForSnackbarObservable;
    @Nonnull
    private final Observable<Void> doneClickObservable;
    @Nonnull
    private final Observable<Void> addClickObservable;

    @Inject
    public AddProductsPresenter(@Nonnull @Named("DoneClickObservable") final Observable<Void> doneClickObservable,
                                @Nonnull @Named("AddClickObservable") final Observable<Void> addClickObservable,
                                @Nonnull @Named("ProductTextInputObservable") final Observable<CharSequence> textChanges,
                                @Nonnull final ProductsDao dao,
                                @Nonnull final UserPreferences userPreferences) {
        this.doneClickObservable = doneClickObservable;
        this.addClickObservable = addClickObservable;

        final Observable<Set<String>> savedItems = Observable.fromCallable(userPreferences::getSuggestedProducts);

        final Observable<List<BaseAdapterItem>> itemsObservable =
                Observable.combineLatest(textChanges, savedItems, (query, strings) -> {
                    final Set<String> suggestedProducts = userPreferences.getSuggestedProducts();
                    final List<BaseAdapterItem> adapterItems = Lists.newArrayList();

                    for (String product : suggestedProducts) {
                        if (product.contains(query)) {
                            adapterItems.add(new SuggestedProductItem(product));
                        }
                    }

                    return adapterItems;
                });

        suggestedProductsObservable = refreshSubject
                .startWith((Object) null)
                .flatMap(o -> itemsObservable);

        final Observable<String> addClick = Observable.merge(doneClickObservable, addClickObservable)
                .withLatestFrom(textChanges, (aVoid, charSequence) -> charSequence.toString());

        addedItemForSnackbarObservable =
                Observable.merge(addClick, clickItemSubject)
                        .filter(name -> !name.isEmpty())
                        .doOnNext(userPreferences::addSuggestedProduct)
                        .flatMap(input -> dao.addNewItemObservable(input)
                                .map(o -> input))
                        .doOnNext(s -> refreshSubject.onNext(null));

        subscription.set(Subscriptions.from(
//                todo add removing items from suggested
        ));

    }

    @Nonnull
    public Observable<Void> closeActivityObservable() {
        return doneClickObservable;
    }


    @Nonnull
    public Observable<String> clearInputObservable() {
        return addClickObservable.map(v -> "");
    }

    @Nonnull
    public Observable<String> getAddedItemForSnackbarObservable() {
        return addedItemForSnackbarObservable.map(s -> s + " added");
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
            return item instanceof SuggestedProductItem;
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
