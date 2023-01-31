package com.example.hanoitower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var myOrangeOval: View
    private lateinit var myBlueOval: View
    private lateinit var myRedOval: View
    private lateinit var spendTime: TextView
    private lateinit var startAgainButt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        myOrangeOval = this.findViewById(R.id.orangeRing)
        myBlueOval = this.findViewById(R.id.blueRing)
        myRedOval = this.findViewById(R.id.redRing)
        spendTime = this.findViewById(R.id.spendTime)
        startAgainButt = this.findViewById(R.id.startAgainButton)

        startAgainButt.setOnClickListener { startAgain() }


    }

    private fun startAgain() {
        //@TODO
    }

}