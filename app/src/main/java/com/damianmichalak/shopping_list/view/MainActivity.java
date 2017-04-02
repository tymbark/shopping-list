package com.damianmichalak.shopping_list.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;
import com.damianmichalak.shopping_list.helper.AuthHelper;
import com.damianmichalak.shopping_list.helper.guava.Strings;
import com.damianmichalak.shopping_list.presenter.MainActivityPresenter;

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
    @Inject
    AuthHelper authHelper;

    @BindView(R.id.main_drawer_layout)
    DrawerLayout drawerLayout;

    private DrawerFragment drawerFragment;

    private ActionBarDrawerToggle drawerToggle;
    private final SerialSubscription subscription = new SerialSubscription();
    private ActionBar supportActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDagger();
        authHelper.onCreate();

        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, ShoppingListFragment.newInstance()).commit();

        setupDrawerAndToolbar();

        subscription.set(Subscriptions.from(
                presenter.getCloseDrawerObservable()
                        .subscribe(o -> drawerLayout.closeDrawers()),
                presenter.getSubscription()
        ));

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
        drawerLayout.addDrawerListener(drawerFragment);

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
        drawerLayout.removeDrawerListener(drawerFragment);
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
//                    todo add dialog are you sure
                    presenter.getRemoveListClickSubject().onNext(null);
                    return true;
                case R.id.rename_list:
//                    todo implement it
                    Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.share_list:
//                    todo implement it
                    Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        authHelper.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        authHelper.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, ShoppingListFragment.newInstance()).commit();
                return true;
            case R.id.history:
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
