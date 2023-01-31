package com.example.hanoitower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources

class MainActivity : AppCompatActivity() {
    private lateinit var myOrangeOval: View
    private lateinit var myBlueOval: View
    private lateinit var myRedOval: View
    private lateinit var spendTime: Chronometer
    private lateinit var startAgainButt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        myOrangeOval = this.findViewById(R.id.orangeRing)
        myBlueOval = this.findViewById(R.id.blueRing)
        myRedOval = this.findViewById(R.id.redRing)

        val temp = MyOnTouchListener()
        myOrangeOval.setOnTouchListener(temp)


        spendTime = this.findViewById(R.id.spendTime)
        startAgainButt = this.findViewById(R.id.startAgainButton)

        startAgainButt.setOnClickListener { startAgain() }


    }
    private fun startAgain() {
        //@TODO
    }


    inner class MyOnTouchListener: View.OnTouchListener {
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {
            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)
            return if (viewToBeDragged == top || owner.childCount == 1) {
                true
            }
            else {
                Toast.makeText(this@MainActivity, getString(R.string.you_can_drag_only_top), Toast.LENGTH_SHORT).show()
                false
            }
        }

    }

    inner class MyOnDragListener: View.OnDragListener {
        override fun onDrag(view: View, dragEvent: DragEvent): Boolean {
            val action = dragEvent.action
            var cancelDrag = false
            val receiveContainer = view as LinearLayout
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    //@TODO
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                DragEvent.ACTION_DROP -> {
                    val topElement: View? = receiveContainer.getChildAt(0) ?: null
                    val draggedRing = dragEvent.localState as ImageView

                    return if (topElement == null) { true }
                    else { topElement.width > draggedRing.width }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    view.background = AppCompatResources.getDrawable(this@MainActivity, R.drawable.tower_shape)
                }
                else -> {}

            }
            return true
        }
    }

}