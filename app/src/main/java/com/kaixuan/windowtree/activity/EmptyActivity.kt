package com.kaixuan.windowtree.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.mWindowInfo

@Window(parentClassName = "com.kaixuan.windowtree.MainActivity",name = "空白界面",index = 2)
class EmptyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_test)
        supportActionBar!!.title = mWindowInfo.name
    }

}
