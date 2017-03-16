package com.damianmichalak.shopping_list.dagger;

import java.util.UUID;

import dagger.Module;
import dagger.Provides;

@Module
public class TokenModule {

    private final String TOKEN = UUID.randomUUID().toString();

    @Provides
    String provideUserToken() {
        return "token12345";
    }

}
