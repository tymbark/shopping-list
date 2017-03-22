package com.damianmichalak.shopping_list.presenter;


import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import java.util.List;

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

    @Inject
    public ProductsPresenter(@Nonnull @Named("AddItemClickObservable") final Observable<Void> clickObservable,
                             @Nonnull @Named("ProductTextInputObservable") final Observable<CharSequence> textChanges,
                             @Nonnull final ShoppingListDao dao) {

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
                                return dao.removeItemByKeyObservable(input);
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
    public Observable<List<BaseAdapterItem>> productsSuggestionsList() {
        return Observable.empty();
    }

}
