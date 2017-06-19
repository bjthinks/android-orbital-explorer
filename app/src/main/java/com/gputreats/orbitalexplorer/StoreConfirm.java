package com.gputreats.orbitalexplorer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class StoreConfirm extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.askStore)
                .setPositiveButton(R.string.askStoreYes,
                        (DialogInterface dialog, int id) ->
                            ((MainActivity) getActivity()).gotoPlayStore())
                .setNegativeButton(R.string.askStoreNo,
                        (DialogInterface dialog, int id) -> {});
        return builder.create();
    }
}
