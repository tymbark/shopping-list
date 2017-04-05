package com.damianmichalak.shopping_list.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;

import com.damianmichalak.shopping_list.R;

import javax.annotation.Nonnull;

import rx.Observer;

public class DialogHelper {

    static public void showNewListNameDialog(@Nonnull final Context context, final Observer<String> observer) {
        showInputDialog(context, observer, R.layout.input_dialog_list, true);
    }

    static public void showUserNameInputDialog(@Nonnull final Context context, final Observer<String> observer) {
        showInputDialog(context, observer, R.layout.input_dialog_username, false);
    }

    private static void showInputDialog(@Nonnull Context context, Observer<String> observer, int resourceId, boolean isCancelEnabled) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(resourceId)
                .create();

        if (isCancelEnabled) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel_button), (d, listener) -> d.dismiss());
        }

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.dialog_positive_button), (d, which) -> {
            final EditText input = (EditText) dialog.findViewById(R.id.dialog_new_list_name);
            if (input != null) {
                observer.onNext(input.getText().toString());
            }
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }

}
