package com.damianmichalak.shopping_list.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ScannerActivity extends BaseActivity {

    private final SerialSubscription subscription = new SerialSubscription();

    public static Intent newIntent(Context context) {
        return new Intent(context, ScannerActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initDagger();
        ButterKnife.bind(this);



        subscription.set(Subscriptions.from(

        ));

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
//            supportActionBar.setTitle(R.string.scanner);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void initDagger() {
        Component component = DaggerScannerActivity_Component
                .builder()
                .applicationComponent(getApplicationComponent())
                .build();
        component.inject(this);
    }

    @ActivityScope
    @dagger.Component(
            dependencies = MainApplication.ApplicationComponent.class
    )
    public interface Component {

        void inject(@Nonnull final ScannerActivity activity);

    }
}
