package com.damianmichalak.shopping_list.view;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.FragmentModule;
import com.damianmichalak.shopping_list.dagger.FragmentScope;
import com.damianmichalak.shopping_list.helper.DialogHelper;
import com.damianmichalak.shopping_list.presenter.DrawerFragmentPresenter;
import com.google.zxing.integration.android.IntentIntegrator;
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

public class DrawerFragment extends BaseFragment implements DrawerLayout.DrawerListener {

    @BindView(R.id.drawer_username)
    TextView username;
    @BindView(R.id.drawer_current_list_name)
    TextView currentListName;
    @BindView(R.id.drawer_add_new_list)
    View addNew;
    @BindView(R.id.drawer_change_username)
    View editUsername;
    @BindView(R.id.drawer_scan_qr)
    View scanQR;

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

        subscription.set(Subscriptions.from(
                presenter.getCurrentListNameObservable()
                        .subscribe(currentListName::setText),
                presenter.getUsernameObservable()
                        .subscribe(username::setText),
                presenter.getSubscription(),
                presenter.getShowChangeUsernameDialogObservable()
                        .subscribe(oldUsername -> DialogHelper.showUserNameInputDialog(getActivity(), presenter.newUsernameObserver(), oldUsername)),
                RxView.clicks(addNew)
                        .subscribe(aVoid -> DialogHelper.showNewListNameDialog(getActivity(), presenter.getAddNewListClickSubject())),
                RxView.clicks(scanQR)
                        .subscribe(o -> scanQR())
        ));

    }

    private void scanQR() {
        new IntentIntegrator(getActivity())
                .setBeepEnabled(false)
                .setPrompt(getString(R.string.scanner_label))
                .initiateScan();
        // result is handled in MainActivity class -> onActivityResult()
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
                .drawerFragmentModule(new DrawerFragmentModule(this))
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
    class DrawerFragmentModule extends FragmentModule {

        public DrawerFragmentModule(BaseFragment fragment) {
            super(fragment);
        }

        @Provides
        @Named("changeUsernameClickObservable")
        Observable<Void> changeUsernameClickObservable() {
            return RxView.clicks(editUsername);
        }


    }

}
