package com.damianmichalak.shopping_list.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;

import com.damianmichalak.shopping_list.R;

import javax.annotation.Nonnull;

public class DialogHelper {

    static public void showNewListNameDialog(@Nonnull final Context context) {

        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton("ok", listener)
                .setNegativeButton("cancel", listener)
                .setView(R.layout.input_dialog)
                .create();

        final EditText input = (EditText) dialog.findViewById(R.id.dialog_new_list_name);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();

    }

}
