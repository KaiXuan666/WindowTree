package com.kaixuan.windowtree

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.WindowTree
import kotlinx.android.synthetic.main.activity_main.*

@Window
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnInit.setOnClickListener {
            WindowTree.init(this.application)
        }
        btnCreateLayout.setOnClickListener {
//            showToast(WindowTree.instance.with(this).clazzName)
            WindowTree.instance.with(this)?.child!!.forEach {
                tabLayout.addTab(tabLayout.newTab().setText(it.name))
            }
        }
        btnDestroy.setOnClickListener {
            WindowTree.destroy()
            tabLayout.removeAllTabs()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
