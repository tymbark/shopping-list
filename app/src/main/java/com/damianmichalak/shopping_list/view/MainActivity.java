package com.damianmichalak.shopping_list.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;
import com.damianmichalak.shopping_list.helper.AuthHelper;
import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.damianmichalak.shopping_list.presenter.MainActivityPresenter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Component component;

    @Inject
    MainActivityPresenter presenter;

    @BindView(R.id.main_root_view)
    View rootView;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout drawerLayout;

    private DrawerFragment drawerFragment;

    private ActionBarDrawerToggle drawerToggle;
    private final SerialSubscription subscription = new SerialSubscription();
    private ActionBar supportActionBar;


    @Nonnull
    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDagger();

        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, ProductsListFragment.newInstance()).commit();

        setupDrawerAndToolbar();
        setupBottomNavigation();

        subscription.set(Subscriptions.from(
                presenter.getCloseDrawerObservable()
                        .subscribe(o -> drawerLayout.closeDrawers()),
                presenter.getQrCodeListError()
                        .subscribe(o -> Snackbar.make(rootView, R.string.main_activity_list_dont_exist_after_qr, Snackbar.LENGTH_LONG).show()),
                presenter.getQrCodeListSuccess()
                        .subscribe(s -> Snackbar.make(rootView, getString(R.string.main_activity_list_added_qr, s), Snackbar.LENGTH_LONG).show()),
                presenter.getShowWelcomeScreenObservable()
                        .subscribe(o -> {
                            finish();
                            startActivity(WelcomeActivity.newIntent(MainActivity.this));
                        }),
                presenter.getSubscription()
        ));

    }

    private void setupBottomNavigation() {
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_shopping_list);
    }

    private void setupDrawerAndToolbar() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close

        );

        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.main_navigation_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_list_share, menu);
        return true;
    }

    public void setToolbarTitle(@Nullable String title) {
        if (supportActionBar != null) {
            supportActionBar.setTitle(Strings.isNullOrEmpty(title) ? getString(R.string.app_name) : title);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.set(Subscriptions.empty());
        drawerLayout.removeDrawerListener(drawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    //    todo move this actions to fragment not MainActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.delete_list:
//                    todo floatingActionButtonAdd dialog are you sure
                    presenter.getRemoveListClickSubject().onNext(null);
                    return true;
                case R.id.rename_list:
//                    todo implement it
                    Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.share_list:
                    startActivity(ShareActivity.newIntent(this));
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, R.string.scanner_failed, Toast.LENGTH_LONG).show();
            } else {
                presenter.getQrCodeShoppingListSubject().onNext(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_shopping_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, ShoppingListFragment.newInstance()).commit();
                return true;
            case R.id.navigation_products:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, ProductsListFragment.newInstance()).commit();
                return true;
            case R.id.navigation_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, HistoryFragment.newInstance()).commit();
                return true;
        }
        return false;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    protected void initDagger() {
        component = DaggerMainActivity_Component
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

        void inject(@Nonnull final MainActivity activity);

    }
}
