package com.damianmichalak.shopping_list.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.model.UserDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class AuthHelper {

    @Nonnull
    private final FirebaseAuth.AuthStateListener mAuthListener;
    @Nonnull
    private final FirebaseAuth mAuth;
    @Nullable
    private Context context;

    @Inject
    public AuthHelper(@Nonnull final UserDao userDao) {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                final String uid = user.getUid();
                userDao.uidObserver().onNext(uid);

                Log.d("AUTH", "onAuthStateChanged:signed_in:" + uid);
            } else {
                // User is signed out
                Log.d("AUTH", "onAuthStateChanged:signed_out");
            }
        };
    }

    public void onStop() {
        context = null;
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onStart(Context context) {
        this.context = context;
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onCreate() {
        mAuth.signInAnonymously()
                .addOnFailureListener(e -> {
                    if (context != null)
                        Toast.makeText(context, R.string.welcome_cannot_login, Toast.LENGTH_LONG).show();
                })
                .addOnCompleteListener(task -> {
                    Logger.log("AUTH", "signInAnonymously:onComplete:" + task.isSuccessful());

                    if (!task.isSuccessful()) {
                        Logger.log("AUTH", "signInAnonymously : " + task.getException());
                    }
                });
    }

}
