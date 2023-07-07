package com.example.stopsmoking4me.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Reminder")
            .setMessage("Do you want to use paid version")
            .setPositiveButton("Subscribe"){_, _ ->

            }
            .setNegativeButton("No"){_, _ ->

            }


        return builder.create()
    }
}