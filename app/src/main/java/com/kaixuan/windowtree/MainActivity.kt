package com.kaixuan.windowtree

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.text.method.ScrollingMovementMethod
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtree_annotation.enums.WindowType
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree
import com.kaixuan.windowtreelibrary.myWindowInfo
import kotlinx.android.synthetic.main.activity_main.*

@Window
class MainActivity : AppCompatActivity() {

    lateinit var with : WindowInfo<*>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_log.movementMethod = ScrollingMovementMethod.getInstance();
        btnInit.setOnClickListener {
            init()
        }
        btnGc.setOnClickListener { System.gc() }
    }

    fun init(){
        WindowTree.init(MyApp.instance)
        with = myWindowInfo()!!
        with.setEventListener { sender, sendData ->

            when(sender){
            // 1、判断是自己的孩子发来的消息
                in with.child -> {
                    when(sendData){
                    // 2、判断发来消息的数据类型，你也可以定义msgCode来进行判断，此处我为了偷懒
                        is String -> {
                            tv_log.append("子模块${sender.getClazzName()}发来了消息，内容=${sendData}\n")
                        }
                        is Int -> {

                        }
                    }
                }
            }
            return@setEventListener "ok 我已收到并处理完毕"
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                this@MainActivity.myWindowInfo()!!.jump(this@MainActivity,p0!!.position,WindowType.FRAGMENTV4)
            }

        })
        btnCreateLayout.setOnClickListener {
            with.child.forEach {
                if (it.windowType == WindowType.FRAGMENTV4){
                    tabLayout.addTab(tabLayout.newTab().setText(it.name))
                }
            }
        }
        btnDestroy.setOnClickListener {
            WindowTree.destroy()
            tabLayout.removeAllTabs()
            frameLayout.removeAllViews()
            System.gc()
            tv_log.text = ""
        }
        myWindowInfo().frameLayoutId = frameLayout.id
        myWindowInfo().jump(this@MainActivity,0,WindowType.FRAGMENTV4)
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
