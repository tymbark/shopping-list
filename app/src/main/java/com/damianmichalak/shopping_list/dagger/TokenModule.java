package com.damianmichalak.shopping_list.dagger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Deprecated
@Module
public class TokenModule {

    private final String TOKEN = UUID.randomUUID().toString();

    @Provides
    @Named("token")
    String provideUserToken() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return "EMPTY"; //TODO MAKE SURE THAT TOKEN IS PRESENT
        }
    }

}
