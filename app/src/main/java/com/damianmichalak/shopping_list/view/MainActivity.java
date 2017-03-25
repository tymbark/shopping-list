package com.damianmichalak.shopping_list.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.dagger.ActivityScope;
import com.damianmichalak.shopping_list.helper.AuthHelper;
import com.damianmichalak.shopping_list.presenter.MainActivityPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Component component;

    @Inject
    MainActivityPresenter presenter;
    AuthHelper authHelper = new AuthHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authHelper.onCreate();
        initDagger();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, ShoppingListFragment.newInstance()).commit();


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
