package com.gputreats.orbitalexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class StoreConfirm extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(R.string.askStore)
                .setPositiveButton(R.string.askStoreYes,
                        (DialogInterface dialog, int id) ->
                            ((MainActivity) requireActivity()).gotoPlayStore())
                .setNegativeButton(R.string.askStoreNo,
                        (DialogInterface dialog, int id) -> {});
        return builder.create();
    }
}
