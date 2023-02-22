package com.dhy.xml_tool

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi


class IncludeParams : FrameLayout {
    private var layoutId = -1

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initParams(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initParams(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initParams(attrs)
    }

    private fun initParams(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.IncludeParams)
        layoutId = a.getResourceId(R.styleable.IncludeParams_layoutId, -1)
        a.recycle()

        readParams()
        if (layoutId != -1) {
            val v = LayoutInflater.from(context).inflate(layoutId, this, false)
            setParams(v)
            addView(v)
        }
    }

    private val map: MutableMap<String, String> = mutableMapOf()
    private fun readParams() {
        val c = contentDescription?.toString()
        c?.split(";")?.forEach {
            val kv = it.split(":")
            map[kv.first()] = kv.last()
        }
    }

    private val reg = Regex("\\$\\{([^}]+)\\}")
    private fun setParams(v: View) {
        if (v is TextView) {
            var text = v.text.toString()
            var m = reg.find(text)
            while (m != null) {
                val value = map[m.groupValues[1]]
                if (value != null) {
                    text = text.replace(m.value, value)
                }
                m = m.next()
            }
            v.text = text
        } else if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                setParams(v.getChildAt(i))
            }
        }
    }
}