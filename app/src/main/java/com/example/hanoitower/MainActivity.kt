package com.example.hanoitower

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources

// Class som implementerer OnTouchListener for å lytte på hendelser på berøringer.
// Denne klassen er en indre klasse til MainActivity og har tilgang til variabler i MainActivity.
// MainActivity utvider AppCompatActivity og har tilgang til alle funksjoner i AppCompatActivity.
class MainActivity : AppCompatActivity() {

    private lateinit var myOrangeOval: ImageView // Forekomst av alle tre ovals og tårnene
    private lateinit var myBlueOval: ImageView
    private lateinit var myRedOval: ImageView
    private lateinit var tower1: LinearLayout
    private lateinit var tower2: LinearLayout
    private lateinit var tower3: LinearLayout

    private lateinit var spendTime: Chronometer // taletid som viser hvor lang tid det tar å løse problemet
    private lateinit var movesText: TextView // som viser antall flyttinger
    private lateinit var startAgainButt: Button // knapp som starter spillet på nytt

    private var moves = 0 // første flyttingen er 0
    private var dragExited = false // Boolean som sjekker om en oval er sluppet utenfor tårnet
    private lateinit var endGamingDialog: EndGamingDialogue // Dialog som vises når spillet er ferdig

    override fun onCreate(savedInstanceState: Bundle?) { // Kjører når aktiviteten starter opp
        super.onCreate(savedInstanceState) // kall til superklassen sin onCreate funksjon
        startProgram() // Kjører startProgram funksjonen
    }

    fun startProgram() { // Funksjon som starter spillet på nytt
        setContentView(R.layout.activity_main) // Setter layoutet til aktiviteten
        initVariables() // Initialiserer variablene
        setTagsForOvals() // Setter tagger for ovals
        setDragListeners(MyOnDragListener()) // Setter lytter for drag events
        setOnTouchListeners(MyOnTouchListener()) // Setter lytter for touch events
        launchTimeCounter() // Starter taletiden
        movesText.text = getString(R.string.set_moves_text, 0) // Setter teksten for antall flyttinger
        startAgainButt.setOnClickListener { startProgram() } // Lytter for klikk på start på nytt knappen
    }
    private fun launchTimeCounter() { // Funksjon som starter taletiden
        spendTime.format = "Brukt tid: 00:%s" // Setter formatet for taletiden
        spendTime.start() // Starter taletiden
    }
    @SuppressLint("ClickableViewAccessibility") // Suppreserer en advarsel  for å bruke onTouchListener
    private fun setOnTouchListeners(myOnTouchListener: MyOnTouchListener) { // Setter lytter for touch events på ovals
        myOrangeOval.setOnTouchListener(myOnTouchListener)
        myBlueOval.setOnTouchListener(myOnTouchListener)
        myRedOval.setOnTouchListener(myOnTouchListener)
    }
    private fun setDragListeners(myOnDragListener: MyOnDragListener) { // Setter lytter for drag events på tårnene
        tower1.setOnDragListener(myOnDragListener)
        tower2.setOnDragListener(myOnDragListener)
        tower3.setOnDragListener(myOnDragListener)
    }
    //
    private fun setTagsForOvals() { // Setter tagger for ovals
        myOrangeOval.tag = R.drawable.orange_ring
        myBlueOval.tag = R.drawable.blue_ring
        myRedOval.tag = R.drawable.red_ring
    }
    private fun initVariables() { // Initialiserer variablene for å kunne bruke dem i hele klassen
        moves = 0
        tower1 = this.findViewById(R.id.linear1)
        tower2 = this.findViewById(R.id.linear2)
        tower3 = this.findViewById(R.id.linear3)

        movesText = this.findViewById(R.id.movesText)
        spendTime = this.findViewById(R.id.spendTime)
        startAgainButt = this.findViewById(R.id.startAgainButton)

        myOrangeOval = this.findViewById(R.id.orangeRing)
        myBlueOval = this.findViewById(R.id.blueRing)
        myRedOval = this.findViewById(R.id.redRing)

    }
    inner class MyOnTouchListener: View.OnTouchListener { // inner klasse som implementerer OnTouchListener for å lytte på touch events
        @SuppressLint("ClickableViewAccessibility") // Suppreserer en advarsel  for å bruke onTouchListener
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {

            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)
            return if (viewToBeDragged == top || owner.childCount == 1) { // Sjekker om ovalen er øverst på tårnet
                val item = ClipData.Item(viewToBeDragged.tag as? CharSequence)
                // Lag en ny ClipData ved å bruke taggen som en etikett, ren tekst MIME-type og
                // det allerede opprettede elementet. Dette oppretter et nytt ClipDescription-objekt i
                // ClipData og setter MIME-typen til "text/plain".
                val dragData = ClipData(
                    viewToBeDragged.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item)
                //2 shadow
                val myShadow = MyDragShadowBuilder(viewToBeDragged)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    //support pre-Nougat versions
                    @Suppress("DEPRECATION")
                    viewToBeDragged.startDrag(dragData, myShadow, viewToBeDragged, 0)
                } else {
                    //supports Nougat and beyond
                    viewToBeDragged.startDragAndDrop(dragData, myShadow, viewToBeDragged, 0)
                }
            }
            else {
                Toast.makeText(this@MainActivity, getString(R.string.you_can_drag_only_top),
                    Toast.LENGTH_SHORT).show() // Viser en toast med en melding
                false
            }

        }

    }
    inner class MyOnDragListener: View.OnDragListener { // inner klasse som implementerer OnDragListener for å lytte på drag events
        override fun onDrag(view: View, dragEvent: DragEvent): Boolean { // overriden metode som lytter på drag events

            val action = dragEvent.action
            val receiveContainer = view as LinearLayout

            when (action) { // Sjekker hvilken drag event som har skjedd
                DragEvent.ACTION_DRAG_STARTED -> { // hvis drag event er startet
                    return true
                }
                DragEvent.ACTION_DRAG_ENTERED -> { // hvis drag event er startet og er innenfor tårnet
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
                }
                DragEvent.ACTION_DRAG_EXITED -> { // hvis drag event er startet og er utenfor tårnet

                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                DragEvent.ACTION_DROP -> { // hvis drag event er sluppet

                    val topElement: View? = receiveContainer.getChildAt(0) ?: null
                    val draggedRing = dragEvent.localState as ImageView

                    if (topElement == null) { // hvis tårnet er tomt så legg ovalen på toppen
                        val parentLayout = draggedRing.parent as LinearLayout
                        parentLayout.removeView(draggedRing)
                        receiveContainer.addView(draggedRing)
                        isGameFinished(receiveContainer)
                        dragExited = true
                        return true }
                    else {
                        if (topElement.width > draggedRing.width) { // hvis ovalen som er på toppen er større enn ovalen som skal legges på toppen
                            val parentLayout = draggedRing.parent as LinearLayout
                            parentLayout.removeView(draggedRing)
                            receiveContainer.addView(draggedRing, 0)
                            isGameFinished(receiveContainer)
                            dragExited = true
                            return true
                        }
                        return false
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> { // hvis drag event er sluttet og ovalen har migrert til et annet tårn

                    if (dragExited) { // hvis ovalen har migrert til et annet tårn så øk antall trekk
                        moves += 1
                        movesText.text = getString(R.string.set_moves_text, moves)
                        dragExited = false // set dragExited til false for å kunne øke antall trekk neste gang
                    }
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape) // set bakgrunnsfarge til tårnet til default bakgrunnsfarge
                }
                else -> {} // hvis ingen av de andre drag eventene har skjedd så gjør ingenting

            }
            return true
        }
    }
    private fun isGameFinished(receiveContainer: LinearLayout) { // funksjon som sjekker om spillet er ferdig og viser en dialog hvis spillet er ferdig
        if (receiveContainer == tower3) {
            if (receiveContainer.childCount > 2) {
                finishProgram()
            }
        }
    }
    private fun finishProgram() { // funksjon som viser en dialog hvis spillet er ferdig

        moves += 1 // øk antall trekk med 1 fordi det mistes et trekk når spillet er ferdig
        movesText.text = getString(R.string.set_moves_text, moves)
        tower3.background = AppCompatResources.getDrawable(this, R.drawable.tower_shape)
        removeGameListeners()
        endGamingDialog = EndGamingDialogue()
        endGamingDialog.show(supportFragmentManager, "EndDialogGaming")
    }
    @SuppressLint("ClickableViewAccessibility") // fjerner warning for at on touch listener er satt på en view
    private fun removeGameListeners() { // funksjon som fjerner alle listeners for å ikke kunne endre på tårnene og ovalene når spillet er ferdig
        tower1.setOnDragListener(null)
        tower2.setOnDragListener(null)
        tower3.setOnDragListener(null)

        myOrangeOval.setOnTouchListener(null)
        myBlueOval.setOnTouchListener(null)
        myRedOval.setOnTouchListener(null)
        spendTime.stop()
    }

    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {
        // inner klasse som lager en drag shadow for ovalene som skal dras rundt på skjermen
        // og legges på tårnene når de dras over tårnene og slippes der de skal legges på tårnene

        private val shadow: Drawable = AppCompatResources.getDrawable(v.context, v.tag as Int)!!

        // Definerer en tilbakeringing som sender dragskyggedimensjonene og berøringspunktet
        // tilbake til systemet.
        override fun onProvideShadowMetrics(size: Point, touch: Point) {

            val width: Int = view.width
            val height: Int = view.height
            shadow.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(width / 2, height / 2) // Berøringspunktet er midten av dragskyggen.
        }

        // Definerer en tilbakeringing som sender dragskyggen til systemet.
        override fun onDrawShadow(canvas: Canvas) {

            // Tegner dragskyggen.
            shadow.draw(canvas)
        }
    }

}

