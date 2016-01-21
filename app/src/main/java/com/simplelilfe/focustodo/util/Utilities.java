package com.simplelilfe.focustodo.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by amitt.
 */
public class Utilities {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
