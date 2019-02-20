package com.kaixuan.windowtree.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window

@Window(parentClass = MainActivity::class,name = "消息",index = 1)
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_test)
    }
}
