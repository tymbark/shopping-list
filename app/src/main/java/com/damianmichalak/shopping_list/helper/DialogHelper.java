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

    static public void showNewListNameDialog(@Nonnull final Context context, final Observer<String> addNewListClickSubject) {

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton("ok", (d, listener) -> d.dismiss())
                .setNegativeButton("cancel", (d, listener) -> d.dismiss())
                .setView(R.layout.input_dialog)
                .create();


        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "okay", (d, which) -> {
            final EditText input = (EditText) dialog.findViewById(R.id.dialog_new_list_name);
            if (input != null) {
                addNewListClickSubject.onNext(input.getText().toString());
            }
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();

    }

}
