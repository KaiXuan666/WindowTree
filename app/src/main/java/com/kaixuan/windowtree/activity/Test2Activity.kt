package com.kaixuan.windowtree.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtree_annotation.model.WindowMeta

@Window(parentClassName = "com.kaixuan.windowtree.MainActivity",index = 1)
class Test2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val list = mutableListOf<Any>()
    }

}
