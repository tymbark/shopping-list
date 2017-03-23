package com.damianmichalak.shopping_list.view;

import android.app.Application;
import android.content.Context;

import com.damianmichalak.shopping_list.dagger.DaoComponent;
import com.damianmichalak.shopping_list.dagger.ForApplication;
import com.damianmichalak.shopping_list.dagger.NetworkModule;
import com.damianmichalak.shopping_list.dagger.SchedulersModule;
import com.damianmichalak.shopping_list.dagger.TokenModule;
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
                .networkModule(new NetworkModule())
                .tokenModule(new TokenModule())
                .mainApplicationModule(new MainApplicationModule(this))
                .build();

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Singleton
    @dagger.Component(
            modules = {
                    TokenModule.class,
                    NetworkModule.class,
                    SchedulersModule.class,
                    MainApplicationModule.class
            }
    )
    public interface ApplicationComponent extends DaoComponent {

        void inject(MainApplication mainApplication);

        UserPreferences UserPreferences();

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
