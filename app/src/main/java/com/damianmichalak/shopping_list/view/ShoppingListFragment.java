package com.damianmichalak.shopping_list.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damianmichalak.shopping_list.R;

public class ShoppingListFragment extends BaseFragment {

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
    }

    @Override
    protected void inject() {
        final Component component = DaggerShoppingListFragment_Component
                .builder()
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
    }

}
