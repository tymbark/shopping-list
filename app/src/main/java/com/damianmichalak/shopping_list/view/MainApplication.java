package com.damianmichalak.shopping_list.view;

import android.app.Application;

import com.damianmichalak.shopping_list.dagger.NetworkModule;
import com.damianmichalak.shopping_list.dagger.SchedulersModule;

public class MainApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerMainApplication_ApplicationComponent.builder().build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @dagger.Component(
            modules = {
                    NetworkModule.class,
                    SchedulersModule.class
            }
    )
    public interface ApplicationComponent {

    }

}
