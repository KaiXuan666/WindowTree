package com.kaixuan.windowtree.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window

@Window(parentClass = MainActivity::class,index = 2)
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
}
