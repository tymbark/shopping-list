package com.damianmichalak.shopping_list.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.model.ShoppingItem;
import com.damianmichalak.shopping_list.presenter.ShoppingListPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jacekmarchwicki.universaladapter.UniversalAdapter;
import com.jacekmarchwicki.universaladapter.ViewHolderManager;
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import dagger.Provides;
import rx.functions.Action1;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ShoppingListFragment extends BaseFragment {

    @Inject
    ShoppingListPresenter presenter;
    @Inject
    ShoppingListManager manager;

    @BindView(R.id.shopping_list_recycler_view)
    RecyclerView recyclerView;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ArrayList<ViewHolderManager> managers = new ArrayList<>();
        managers.add(manager);
        final RxUniversalAdapter adapter = new RxUniversalAdapter(managers);
        recyclerView.setAdapter(adapter);

        subscription.set(Subscriptions.from(
                presenter.getShoppingListObservable()
                        .subscribe(adapter)
        ));


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void inject() {
        final Component component = DaggerShoppingListFragment_Component
                .builder()
                .module(new Module(this))
                .component(((MainActivity) getActivity()).getComponent())
                .build();

        component.inject(this);
    }

    @dagger.Component(
            dependencies = MainActivity.Component.class,
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
        public LayoutInflater provideLayoutInflater() {
            return fragment.getActivity().getLayoutInflater();
        }
    }

}
