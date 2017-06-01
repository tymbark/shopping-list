package com.damianmichalak.shopping_list.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.ImageView;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;
import com.damianmichalak.shopping_list.helper.QRHelper;
import com.damianmichalak.shopping_list.presenter.ShareActivityPresenter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class ShareActivity extends BaseActivity {

    private final SerialSubscription subscription = new SerialSubscription();

    @BindView(R.id.activity_share_qr_view)
    ImageView qrView;
    @BindView(R.id.activity_share_qr_list_name)
    TextView name;

    @Inject
    ShareActivityPresenter presenter;

    public static Intent newIntent(Context context) {
        return new Intent(context, ShareActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initDagger();
        ButterKnife.bind(this);

        subscription.set(Subscriptions.from(
                presenter.getCurrentListKeyObservable()
                        .subscribe(listId -> QRHelper.generateQR(listId, qrView)),
                presenter.getCurrentListNameObservable()
                        .subscribe(listName -> name.setText(listName))
        ));

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(R.string.activity_share_title);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.set(Subscriptions.empty());
    }

    @Override
    protected void initDagger() {
        Component component = DaggerShareActivity_Component
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

        void inject(@Nonnull final ShareActivity activity);

    }
}
