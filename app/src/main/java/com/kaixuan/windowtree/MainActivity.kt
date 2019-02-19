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

        btnInit.setOnClickListener {
            init()
        }
    }

    fun init(){
        WindowTree.init(MyApp.instance)
        with = WindowTree.instance.with(this)!!
        tv_log.movementMethod = ScrollingMovementMethod.getInstance();
        with.onEventListener = { windowInfo: WindowInfo<*>, any: Any? ->
            when(windowInfo){
                // 1、判断是自己的孩子发来的消息
                in with.child -> {
                    when(any){
                        // 2、判断发来消息的数据类型，你也可以定义msgCode来进行判断，此处我为了偷懒
                        is String -> {
                            tv_log.append("子模块${windowInfo.getClazzName()}发来了消息，内容=${any}\n")
                        }
                        is Int -> {

                        }
                    }
                }
            }
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
            tv_log.text = ""
        }
        myWindowInfo()!!.frameLayoutId = frameLayout.id
        myWindowInfo()!!.jump(this@MainActivity,0,WindowType.FRAGMENTV4)
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
