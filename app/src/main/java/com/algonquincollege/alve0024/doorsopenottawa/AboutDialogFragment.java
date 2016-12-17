/*********************************************************************************************
 *   Doors Open Ottawa - List information about the Buildings with the Doors Open            *
 *                                                                                           *
 *   @author Leonardo Alps (alve0024@algonquinlive.com)                                      *
 *                                                                                           *
 *   Supervision: Gerald.Hurdle@AlgonquinCollege.com                                         *
 *                                                                                           *
 *   Algonquin College - All right reserved!                                                 *
 *                                                                                           *
 *********************************************************************************************/

package com.algonquincollege.alve0024.doorsopenottawa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Inflate a dialog box with details about the author
 */
public class AboutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.action_about)
                .setMessage(R.string.author)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
