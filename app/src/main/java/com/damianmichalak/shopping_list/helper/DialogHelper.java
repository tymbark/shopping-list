package com.damianmichalak.shopping_list.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.helper.guava.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import rx.Observer;

public class DialogHelper {

    static public void showNewListNameDialog(@Nonnull final Context context, @Nonnull final Observer<String> observer) {
        showInputDialog(context, observer, R.layout.input_dialog_list, true, null);
    }

    static public void showUserNameInputDialog(@Nonnull final Context context, final Observer<String> observer, @Nullable final String hint) {
        showInputDialog(context, observer, R.layout.input_dialog_username, false, hint);
    }

    private static void showInputDialog(@Nonnull final Context context, @Nonnull final Observer<String> observer, int resourceId, boolean isCancelEnabled, @Nullable String hint) {
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

        final EditText input = (EditText) dialog.findViewById(R.id.dialog_new_list_name);
        if (input != null && Strings.isNotNullAndNotEmpty(hint)) {
            input.setText(hint);
        }
    }

}
