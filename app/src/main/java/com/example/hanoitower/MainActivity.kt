package com.example.hanoitower

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources

class MainActivity : AppCompatActivity() {
    private lateinit var myOrangeOval: ImageView
    private lateinit var myBlueOval: ImageView
    private lateinit var myRedOval: ImageView
    private lateinit var tower1: LinearLayout
    private lateinit var tower2: LinearLayout
    private lateinit var tower3: LinearLayout
    private lateinit var spendTime: TextView
    private lateinit var movesText: TextView
    private lateinit var startAgainButt: Button


    private var moves = 0
    private var drag_exited = false

    @SuppressLint("ClickableViewAccessibility") //@TODO fix
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tower1 = this.findViewById(R.id.linear1)
        tower1.setOnDragListener(MyOnDragListener())

        tower2 = this.findViewById(R.id.linear2)
        tower2.setOnDragListener(MyOnDragListener())

        tower3 = this.findViewById(R.id.linear3)
        tower3.setOnDragListener(MyOnDragListener())

        myOrangeOval = this.findViewById(R.id.orangeRing)
        myBlueOval = this.findViewById(R.id.blueRing)
        myRedOval = this.findViewById(R.id.redRing)
        //val temp = MyOnTouchListener()

        myRedOval.setOnTouchListener(MyOnTouchListener())
        //myRedOval.setOnDragListener(MyOnDragListener())

        myBlueOval.setOnTouchListener(MyOnTouchListener())

        myOrangeOval.setOnTouchListener(MyOnTouchListener())

        movesText = this.findViewById(R.id.movesText)
        spendTime = this.findViewById(R.id.spendTime)
        startAgainButt = this.findViewById(R.id.startAgainButton)

        startAgainButt.setOnClickListener { startAgain() }


    }

    private fun startAgain() {
        //@TODO
    }


    inner class MyOnTouchListener: View.OnTouchListener {
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {
            Log.i("test", "onTouch!")
            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)
            return if (viewToBeDragged == top || owner.childCount == 1) {
                Log.i("test", "OnTouch = True")
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
                Log.i("test", "OnTouch = False")
                false
            }
        }


    }

    inner class MyOnDragListener: View.OnDragListener {
        override fun onDrag(view: View, dragEvent: DragEvent): Boolean {
            Log.i("test", "onDrag!")
            val action = dragEvent.action
            var cancelDrag = false
            val receiveContainer = view as LinearLayout

            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.i("test", "Action drag started!")
                    //@TODO
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.i("test", "Action drag entered!")
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
                }
                DragEvent.ACTION_DRAG_EXITED -> {

                    drag_exited = true

                    Log.i("test", "Action drag exited!")
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                DragEvent.ACTION_DROP -> {
                    Log.i("test", "Action drop!")
                    val topElement: View? = receiveContainer.getChildAt(0) ?: null
                    val draggedRing = dragEvent.localState as ImageView

                    if (topElement == null) {
                        val parentLayout = draggedRing.parent as LinearLayout
                        parentLayout.removeView(draggedRing)
                        receiveContainer.addView(draggedRing)
                        Log.i("test", "OnDragDrop = True (Null)")
                        return true }
                    else {
                        if (topElement.width > draggedRing.width) {
                            Log.i("test", "OnDragDrop top>Dragged = ${topElement.width > draggedRing.width}")
                            val parentLayout = draggedRing.parent as LinearLayout
                            parentLayout.removeView(draggedRing)
                            receiveContainer.addView(draggedRing, 0)
                            return true
                        }
                        Log.i("test", "OnDragDrop false")
                        return false
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.i("test", "Action drag ended!")

                    if (drag_exited) {

                        moves += 1

                        Log.i("test", "Moves: $moves")

                        movesText.setText("Moves: $moves")

                        drag_exited = false

                    }

                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                else -> {Log.i("test", "Else!")}

            }
            return true
        }
    }

    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

        private val shadow = ColorDrawable((v as ImageView).solidColor)

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