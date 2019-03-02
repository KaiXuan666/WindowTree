package com.kaixuan.windowtree.activity

import android.os.Bundle
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.mWindowInfo

@Window(parentClass = MainActivity::class,name = "新闻列表",index = 1)
class NewsListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_test)
        supportActionBar!!.title = mWindowInfo.name
    }
}
