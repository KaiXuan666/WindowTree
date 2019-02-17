package com.kaixuan.windowtree

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.WindowTree

@Window
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowTree.init(this.application)
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        WindowTree.init(this.application)
    }
}
