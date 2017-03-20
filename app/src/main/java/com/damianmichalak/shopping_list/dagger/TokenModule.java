package com.damianmichalak.shopping_list.dagger;

import java.util.UUID;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class TokenModule {

    private final String TOKEN = UUID.randomUUID().toString();

    @Provides
    @Named("token")
    String provideUserToken() {
        return "token12345";
    }

}
