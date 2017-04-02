package com.damianmichalak.shopping_list.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;
import com.damianmichalak.shopping_list.helper.RxSnackbar;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.presenter.ProductsPresenter;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Provides;
import rx.Observable;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ProductsActivity extends BaseActivity {

    @BindView(R.id.activity_product_root_view)
    View rootView;
    @BindView(R.id.activity_product_input)
    EditText input;
    @BindView(R.id.activity_product_add)
    View add;
    @BindView(R.id.activity_product_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_product_done)
    Button done;

    @Inject
    ProductsPresenter presenter;
    @Inject
    ProductsListManager manager;

    private final SerialSubscription subscription = new SerialSubscription();

    public static Intent newIntent(Context context) {
        return new Intent(context, ProductsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        initDagger();

        recyclerView.setLayoutManager(new FlexboxLayoutManager());
        final RxUniversalAdapter adapter = new RxUniversalAdapter(Lists.<ViewHolderManager>newArrayList(manager));
        recyclerView.setAdapter(adapter);

        subscription.set(Subscriptions.from(
                presenter.getSubscription(),
                presenter.getSuggestedProductsObservable().subscribe(adapter),
                presenter.getAddedItemForSnackbarObservable().subscribe(RxSnackbar.showSnackbar(rootView)),
                presenter.closeActivityObservable().subscribe(o -> finish()),
                presenter.clearInputObservable().subscribe(input::setText)
        ));

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(R.string.products_activity_title);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    @Override
    protected void initDagger() {
        final Component component = DaggerProductsActivity_Component.builder()
                .applicationComponent(getApplicationComponent())
                .module(new Module(this))
                .build();

        component.inject(this);
    }

    @ActivityScope
    @dagger.Component(
            modules = Module.class,
            dependencies = MainApplication.ApplicationComponent.class
    )
    public interface Component {

        void inject(@Nonnull final ProductsActivity activity);

    }

    @dagger.Module
    class Module {

        private final ProductsActivity productsActivity;

        public Module(ProductsActivity productsActivity) {
            this.productsActivity = productsActivity;
        }

        @Nonnull
        @Provides
        @Named("AddClickObservable")
        Observable<Void> provideAddClickObservable() {
            return RxView.clicks(add).share();
        }

        @Nonnull
        @Provides
        @Named("DoneClickObservable")
        Observable<Void> provideDoneClickObservable() {
            return RxView.clicks(done).share();
        }

        @Nonnull
        @Provides
        @Named("ProductTextInputObservable")
        Observable<CharSequence> provideProductTextInputObservable() {
            return RxTextView.textChanges(input);
        }


    }

}
