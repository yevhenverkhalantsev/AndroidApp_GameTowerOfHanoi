package com.example.hanoitower

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EndGamingDialogue: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(getString(R.string.u_won_message))
                .setPositiveButton(getString(R.string.start_again)
                ) { _, _ ->
                    (activity as MainActivity).startProgram()
                }
                .setNegativeButton("") { _, _ -> }
            builder.create()
        }
    }
}