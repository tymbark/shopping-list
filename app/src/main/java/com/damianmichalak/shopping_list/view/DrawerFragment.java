package com.damianmichalak.shopping_list.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.FragmentScope;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DrawerFragment extends BaseFragment {

    @BindView(R.id.drawer_username)
    TextView username;
    @BindView(R.id.drawer_shopping_list)
    ListView shoppingList;

    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void inject() {
        DaggerDrawerFragment_DrawerFragmentComponent.builder().build().inject(this);
    }

    @FragmentScope
    @dagger.Component(dependencies = MainApplication.ApplicationComponent.class)
    interface DrawerFragmentComponent {
        void inject(DrawerFragment drawer);
    }

}
