package com.damianmichalak.shopping_list.helper;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nonnull;

public class AuthHelper {

    @Nonnull
    private final FirebaseAuth.AuthStateListener mAuthListener;
    @Nonnull
    private final FirebaseAuth mAuth;

    public AuthHelper() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                final String uid = user.getUid();

                Log.d("AUTH", "onAuthStateChanged:signed_in:" + uid);
            } else {
                // User is signed out
                Log.d("AUTH", "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    public void onStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onCreate() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    Log.d("AUTH", "signInAnonymously:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w("AUTH", "signInAnonymously", task.getException());
//                        Toast.makeText(MainActivity.this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
