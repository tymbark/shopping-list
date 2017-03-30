package com.damianmichalak.shopping_list.helper;

import android.support.design.widget.Snackbar;
import android.view.View;

import rx.functions.Action1;

public class RxSnackbar {

    public static Action1<String> showSnackbar(final View rootView) {
        return text -> Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT).show();
    }

}
