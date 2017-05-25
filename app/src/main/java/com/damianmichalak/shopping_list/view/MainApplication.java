package com.damianmichalak.shopping_list.view;

import android.app.Application;
import android.content.Context;

import com.damianmichalak.shopping_list.dagger.ForApplication;
import com.damianmichalak.shopping_list.dagger.NetworkModule;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.HistoryDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.damianmichalak.shopping_list.model.ProductsDao;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.damianmichalak.shopping_list.model.UserDao;
import com.damianmichalak.shopping_list.model.UserPreferences;

import javax.inject.Singleton;

import dagger.Provides;

public class MainApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerMainApplication_ApplicationComponent
                .builder()
                .mainApplicationModule(new MainApplicationModule(this))
                .build();

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Singleton
    @dagger.Component(
            modules = {
                    NetworkModule.class,
                    MainApplicationModule.class
            }
    )
    public interface ApplicationComponent {

        void inject(MainApplication mainApplication);

        UserPreferences UserPreferences();

        HistoryDao historyDao();

        CurrentListDao currentListDao();

        ProductsDao productsDao();

        ListsDao listDao();

        UserDao userDao();

    }

    @dagger.Module
    class MainApplicationModule {
        private final MainApplication application;

        MainApplicationModule(MainApplication application) {
            this.application = application;
        }

        @Provides
        @ForApplication
        Context provideAppContext() {
            return application;
        }

    }

}
