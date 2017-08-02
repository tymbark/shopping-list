package com.damianmichalak.shopping_list.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.FragmentScope;
import com.damianmichalak.shopping_list.helper.DialogHelper;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.presenter.ShoppingListPresenter;
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import dagger.Provides;
import rx.Observable;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ShoppingListFragment extends BaseFragment {

    public static final String TAG = "ShoppingListFragment";
    @Inject
    ShoppingListPresenter presenter;
    @Inject
    ShoppingListItemManager manager;

    @BindView(R.id.shopping_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.shopping_list_add_button)
    View floatingActionButtonAdd;
    @BindView(R.id.shopping_list_empty_products_view)
    View emptyListView;

    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();

    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.shopping_list_toolbar_title));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final RxUniversalAdapter adapter = new RxUniversalAdapter(Lists.newArrayList(manager));
        recyclerView.setAdapter(adapter);

        subscription.set(Subscriptions.from(
                presenter.getListObservable()
                        .subscribe(adapter),
                presenter.getEmptyListObservable()
                        .subscribe(RxView.visibility(emptyListView)),
                presenter.getShowProductsObservable()
                        .subscribe(o -> ((MainActivity) getActivity())
                                .selectItemNavigation(R.id.navigation_products)),
//                presenter.getFloatingActionButtonObservable()
//                        .subscribe(RxView.visibility(floatingActionButtonAdd)),
                presenter.getShowNewListDialogObservable()
                        .subscribe(o -> DialogHelper.showNewListNameDialog(getActivity(), presenter.getAddNewListClickSubject())),
                presenter.getListNameObservable()
                        .subscribe(((MainActivity) getActivity())::setToolbarSubtitle),
                presenter.getSubscription()
        ));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void initDagger() {
        final Component component = DaggerShoppingListFragment_Component
                .builder()
                .module(new Module(this))
                .applicationComponent(((BaseActivity) getActivity()).getApplicationComponent())
                .build();

        component.inject(this);
    }

    @FragmentScope
    @dagger.Component(
            dependencies = MainApplication.ApplicationComponent.class,
            modules = Module.class
    )
    public interface Component {

        void inject(ShoppingListFragment shoppingListFragment);
    }

    @dagger.Module
    public class Module {

        private final Fragment fragment;

        public Module(Fragment fragment) {
            this.fragment = fragment;
        }

        @Provides
        @Nonnull
        public LayoutInflater provideLayoutInflater() {
            return fragment.getActivity().getLayoutInflater();
        }

        @Provides
        @Nonnull
        @Named("AddListEmptyClickObservable")
        Observable<Void> provideAddListEmptyClickObservable() {
            return RxView.clicks(emptyListView).share();
        }

        @Provides
        @Nonnull
        @Named("AddListClickObservable")
        Observable<Void> provideAddListClickObservable() {
            return RxView.clicks(floatingActionButtonAdd).share();
        }

    }

}
