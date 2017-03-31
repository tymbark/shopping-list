package com.damianmichalak.shopping_list.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.FragmentScope;
import com.damianmichalak.shopping_list.helper.DialogHelper;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.presenter.DrawerFragmentPresenter;
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import dagger.Provides;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class DrawerFragment extends BaseFragment implements DrawerLayout.DrawerListener{

    @BindView(R.id.drawer_username)
    TextView username;
    @BindView(R.id.drawer_shopping_list)
    RecyclerView recyclerView;
    @BindView(R.id.drawer_add_new_list)
    View addNew;

    @Inject
    DrawerFragmentPresenter presenter;

    @Inject
    DrawerItemManager manager;

    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final RxUniversalAdapter adapter = new RxUniversalAdapter(Lists.newArrayList(manager));
        recyclerView.setAdapter(adapter);

        subscription.set(Subscriptions.from(
                presenter.getListObservable()
                        .subscribe(adapter),
                presenter.getSubscription(),
                RxView.clicks(addNew)
                        .subscribe(aVoid -> DialogHelper.showNewListNameDialog(getActivity(), presenter.getAddNewListClickSubject()))
        ));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void initDagger() {
        DaggerDrawerFragment_DrawerFragmentComponent
                .builder()
                .applicationComponent(((BaseActivity) getActivity()).getApplicationComponent())
                .drawerFragmentModule(new DrawerFragmentModule())
                .build()
                .inject(this);
    }

//todo find a better solution for passing this event to presenter
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        presenter.refreshList().onNext(null);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @FragmentScope
    @dagger.Component(
            dependencies = MainApplication.ApplicationComponent.class,
            modules = DrawerFragmentModule.class)
    interface DrawerFragmentComponent {
        void inject(DrawerFragment drawer);
    }

    @dagger.Module
    class DrawerFragmentModule {

        @Provides
        @Named("AddNewListClickObservable")
        Observable<Void> provideAddNewListClickObservable() {
            return Observable.empty();
        }


    }

}
