package com.damianmichalak.shopping_list.helper;

import android.content.Context;
import android.net.ConnectivityManager;

import javax.annotation.Nonnull;


public class ConnectivityHelper {

    @Nonnull
    private final Context context;

    public ConnectivityHelper(@Nonnull Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
