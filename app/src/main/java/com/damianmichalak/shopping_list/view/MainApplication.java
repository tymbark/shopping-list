package com.damianmichalak.shopping_list.view;

import android.app.Application;

import com.damianmichalak.shopping_list.dagger.DaoComponent;
import com.damianmichalak.shopping_list.dagger.NetworkModule;
import com.damianmichalak.shopping_list.dagger.SchedulersModule;
import com.damianmichalak.shopping_list.dagger.TokenModule;
import com.damianmichalak.shopping_list.model.ShoppingListDao;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class MainApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Inject
    @Named("token")
    String token;

    @Inject
    ShoppingListDao dao;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerMainApplication_ApplicationComponent
                .builder()
                .networkModule(new NetworkModule())
                .tokenModule(new TokenModule())
                .build();

//        dao.getListObservable().subscribe();

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Singleton
    @dagger.Component(
            modules = {
                    TokenModule.class,
                    NetworkModule.class,
                    SchedulersModule.class
            }
    )
    public interface ApplicationComponent extends DaoComponent {

        void inject(MainApplication mainApplication);

    }

}
