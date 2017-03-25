package com.damianmichalak.shopping_list.view;


import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StyleRes;

public class AddShoppingListDialog extends AlertDialog {

    protected AddShoppingListDialog(Context context) {
        super(context);
    }

    protected AddShoppingListDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected AddShoppingListDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

}
