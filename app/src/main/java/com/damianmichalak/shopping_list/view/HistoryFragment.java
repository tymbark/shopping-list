package com.damianmichalak.shopping_list.view;

import android.content.res.Resources;
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
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.jacekmarchwicki.universaladapter.rx.RxUniversalAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import dagger.Provides;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class HistoryFragment extends BaseFragment {

    @BindView(R.id.history_list_recycler_view)
    RecyclerView recyclerView;

    @Inject
    HistoryPresenter presenter;
    @Inject
    HistoryProductsListManager manager;

    @Nonnull
    private final SerialSubscription subscription = new SerialSubscription();

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final RxUniversalAdapter adapter = new RxUniversalAdapter(Lists.newArrayList(manager));
        recyclerView.setAdapter(adapter);

        subscription.set(Subscriptions.from(
                presenter.getHistoryProductsForCurrentList()
                        .subscribe(adapter)
        ));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void initDagger() {
        final HistoryFragment.Component component = DaggerHistoryFragment_Component
                .builder()
                .module(new HistoryFragment.Module(this))
                .applicationComponent(((BaseActivity) getActivity()).getApplicationComponent())
                .build();

        component.inject(this);

    }

    @FragmentScope
    @dagger.Component(
            dependencies = MainApplication.ApplicationComponent.class,
            modules = HistoryFragment.Module.class
    )
    public interface Component {
        void inject(HistoryFragment historyFragment);
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
        Resources provideResources() {
            return fragment.getResources();
        }

    }
}
