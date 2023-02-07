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

// Class that represents the main activity of the app (the game)
class MainActivity : AppCompatActivity() {

    private lateinit var myOrangeOval: ImageView // Instance of orange oval
    private lateinit var myBlueOval: ImageView // Instance of blue oval
    private lateinit var myRedOval: ImageView // Instance of red oval
    private lateinit var tower1: LinearLayout // Instance of tower 1
    private lateinit var tower2: LinearLayout // Instance of tower 2
    private lateinit var tower3: LinearLayout // Instance of tower 3
    private lateinit var spendTime: Chronometer // Chronometer that counts the time spent on the game
    private lateinit var movesText: TextView // TextView that shows the number of moves
    private lateinit var startAgainButt: Button // Button that starts the game again

    private var moves = 0 // Number of moves
    private var dragExited = false // Boolean that checks if the oval has been dragged out of the tower
    private lateinit var endGamingDialog: EndGamingDialogue

    // Function that called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call onCreate from the super class (AppCompatActivity)
        startProgram() // Start the program
    }

    // Function that sets the initial conditions for the game
    fun startProgram() {
        setContentView(R.layout.activity_main) // Set the layout to activity_main.xml
        initVariables()
        setTagsForOvals()
        setDragListeners(MyOnDragListener())
        setOnTouchListeners(MyOnTouchListener())
        launchTimeCounter()
        movesText.text = getString(R.string.set_moves_text, 0)
        // Set the listener for the button that starts the game again
        startAgainButt.setOnClickListener { startProgram() }
    }
    // Function that sets the initial conditions for the chronometer
    private fun launchTimeCounter() {
        spendTime.format = "Brukt tid: 00:%s" // Set the format of the chronometer output
        spendTime.start() // Start the chronometer
    }

    // Listener for the event on touching ovals
    @SuppressLint("ClickableViewAccessibility") // Suppress the warning about the onTouch function
    private fun setOnTouchListeners(myOnTouchListener: MyOnTouchListener) {
        myOrangeOval.setOnTouchListener(myOnTouchListener) // Set the listener for the orange oval
        myBlueOval.setOnTouchListener(myOnTouchListener) // Set the listener for the blue oval
        myRedOval.setOnTouchListener(myOnTouchListener) // Set the listener for the red oval
    }

    // Listener for the event on dragging ovals
    private fun setDragListeners(myOnDragListener: MyOnDragListener) {
        tower1.setOnDragListener(myOnDragListener)
        tower2.setOnDragListener(myOnDragListener)
        tower3.setOnDragListener(myOnDragListener)
    }
    //
    private fun setTagsForOvals() {
        myOrangeOval.tag = R.drawable.orange_ring
        myBlueOval.tag = R.drawable.blue_ring
        myRedOval.tag = R.drawable.red_ring
    }

    // Function that initializes the variables and find by the id in the layout
    private fun initVariables() {
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

    // Listener for the event on touching ovals
    inner class MyOnTouchListener: View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility") // Suppress the warning about the onTouch function
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {

            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)
            return if (viewToBeDragged == top || owner.childCount == 1) {
                // Create a new ClipData.Item from the ImageView object's tag.
                val item = ClipData.Item(viewToBeDragged.tag as? CharSequence)
                // Create a new ClipData using the tag as a label, the plain text MIME type, and
                // the already-created item. This creates a new ClipDescription object within the
                // ClipData and sets its MIME type to "text/plain".
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
                Toast.makeText(this@MainActivity, getString(R.string.you_can_drag_only_top), Toast.LENGTH_SHORT).show()
                false
            }

        }

    }

    // Listener for the event on dragging ovals
    inner class MyOnDragListener: View.OnDragListener {
        override fun onDrag(view: View, dragEvent: DragEvent): Boolean {

            val action = dragEvent.action
            val receiveContainer = view as LinearLayout

            when (action) { // Check the action of the drag event
                DragEvent.ACTION_DRAG_STARTED -> { // If the drag event is started
                }
                DragEvent.ACTION_DRAG_ENTERED -> { // If the drag event is entering to other tower space
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
                }
                DragEvent.ACTION_DRAG_EXITED -> { // If the drag event is exiting from the current tower space

                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                DragEvent.ACTION_DROP -> { // If the drag event is dropped

                    val topElement: View? = receiveContainer.getChildAt(0) ?: null
                    val draggedRing = dragEvent.localState as ImageView

                    if (topElement == null) {
                        val parentLayout = draggedRing.parent as LinearLayout
                        parentLayout.removeView(draggedRing)
                        receiveContainer.addView(draggedRing)
                        isGameFinished(receiveContainer)
                        dragExited = true
                        return true }
                    else {
                        if (topElement.width > draggedRing.width) {
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
                DragEvent.ACTION_DRAG_ENDED -> { // If the drag event is ended

                    if (dragExited) { // If the drag event is ended and the oval has migrated to another tower space
                        moves += 1 // Increase the number of moves
                        movesText.text = getString(R.string.set_moves_text, moves) // Set the text of the moves text view
                        dragExited = false // Set the dragExited variable to false (Now any oval has not migrated to another tower space)
                    }
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                else -> {}

            }
            return true
        }
    }

    // Function to check if the game is finished
    private fun isGameFinished(receiveContainer: LinearLayout) {
        if (receiveContainer == tower3) {
            if (receiveContainer.childCount > 2) {
                finishProgram()
            }
        }
    }

    // Function to finish the game
    private fun finishProgram() {

        moves += 1 // Increase the last move
        movesText.text = getString(R.string.set_moves_text, moves)
        tower3.background = AppCompatResources.getDrawable(this, R.drawable.tower_shape) //
        // Set the background of the tower3 to the default background
        removeGameListeners()
        endGamingDialog = EndGamingDialogue()
        endGamingDialog.show(supportFragmentManager, "EndDialogGaming")
    }

    // Function to remove the listeners
    @SuppressLint("ClickableViewAccessibility")
    private fun removeGameListeners() {
        tower1.setOnDragListener(null)
        tower2.setOnDragListener(null)
        tower3.setOnDragListener(null)

        myOrangeOval.setOnTouchListener(null)
        myBlueOval.setOnTouchListener(null)
        myRedOval.setOnTouchListener(null)
        spendTime.stop()
    }

    // Class to create the shadow of the oval
    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

        private val shadow: Drawable = AppCompatResources.getDrawable(v.context, v.tag as Int)!!

        // Defines a callback that sends the drag shadow dimensions and touch point
        // back to the system.
        override fun onProvideShadowMetrics(size: Point, touch: Point) {

            // Set the width of the shadow to half the width of the original View.
            val width: Int = view.width

            // Set the height of the shadow to half the height of the original View.
            val height: Int = view.height

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the
            // same as the Canvas that the system provides. As a result, the drag shadow
            // fills the Canvas.

            shadow.setBounds(0, 0, width, height)

            // Set the size parameter's width and height values. These get back to
            // the system through the size parameter.
            size.set(width, height)

            // Set the touch point's position to be in the middle of the drag shadow.
            touch.set(width / 2, height / 2)
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system
        // constructs from the dimensions passed to onProvideShadowMetrics().
        override fun onDrawShadow(canvas: Canvas) {

            // Draw the ColorDrawable on the Canvas passed in from the system.
            shadow.draw(canvas)
        }
    }

}

