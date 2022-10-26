package com.example.crimemap_up920915;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class ExitOnClick implements AlertDialog.OnClickListener
{
    @Override
    public void onClick(DialogInterface dialog, int which) {

        System.exit(0);
    }
}
