package com.example.hanoitower

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EndGamingDialogue: DialogFragment() { // class er nødvendig for å vise en dialogboksvindu

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let { // let er en funksjon som lar deg utføre en operasjon på et objekt hvis det ikke er null
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(getString(R.string.u_won_message))
                .setPositiveButton(getString(R.string.start_again)
                ) { _, _ ->
                    (activity as MainActivity).startProgram()
                }
                .setNegativeButton("") { _, _ -> } // tom string for å ikke vise noe tekst på knappen
            builder.create()// returnerer en dialogboks
        }
    }
}