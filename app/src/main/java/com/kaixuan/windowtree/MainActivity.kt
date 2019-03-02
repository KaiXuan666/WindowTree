package com.kaixuan.windowtree

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.kaixuan.windowtree.activity.BaseActivity
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtree_annotation.enums.WindowType
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree
import com.kaixuan.windowtreelibrary.mWindowInfo
import com.kaixuan.windowtreelibrary.model.UnReadCountEvent
import kotlinx.android.synthetic.main.activity_main.*

@Window
class MainActivity : BaseActivity() {

    lateinit var with : WindowInfo<*>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_log.movementMethod = ScrollingMovementMethod.getInstance();
        btnInit.setOnClickListener {
            init()
        }
        btnCreateLayout.setOnClickListener {
            if (WindowTree.hasInit){
                bindTabLayout()
                initActivityButton()
            }else{
                showToast("请先初始化")
            }
        }
        btnGc.setOnClickListener { System.gc() }
        btnDestroy.setOnClickListener {
            release()
        }
    }

    fun init(){
        if (WindowTree.hasInit){
            showToast("已经初始化过")
            return
        }
        WindowTree.init(MyApp.instance)
        with = mWindowInfo
        mWindowInfo.frameLayoutId = frameLayout.id
        initEventListener()

    }

    fun initEventListener(){
        with.setEventListener { sender, sendData ->

            if (sendData is UnReadCountEvent){
                tv_log.append("未读消息：${sender.name}的未读消息发生变化，数量变化=${sendData.change}\n")
                updateUnReadCount()
                return@setEventListener null
            }
            when(sender){
                // 1、判断是自己的孩子发来的消息
                in with.child -> {
                    when(sendData){
                        // 2、判断发来消息的数据类型，你也可以定义msgCode或其他数据类型来进行判断，此处我为了偷懒
                        is String -> {
                            tv_log.append("子模块${sender.name}发来了消息，内容=${sendData}\n")
                        }
                        is Int -> {

                        }
                    }
                }
            }

            return@setEventListener "ok 我已收到并处理完毕" // 支持返回给消息发送者一个回信
        }
    }

    fun bindTabLayout(){
        if (tabLayout.tabCount != 0){
            showToast("不能重复添加tab")
            return
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                mWindowInfo.jump(tab!!.position,WindowType.FRAGMENTV4)
            }
        })

        // 自动布局到TabLayout，两种方式都可以实现
//        with.child.forEach {
//            if (it.windowType == WindowType.FRAGMENTV4){
//                tabLayout.addTab(tabLayout.newTab().setText(it.name))
//            }
//        }
        mWindowInfo.filterChildByWindowType(WindowType.FRAGMENTV4).forEach { window ->
            tabLayout.addTab(tabLayout.newTab().setText(window.name).setTag(window))
        }
    }

    fun initActivityButton(){
        llActivity.visibility = View.VISIBLE
        if (llActivity.childCount == 0){
            // 过滤子Window自动进行布局
            mWindowInfo.filterChildByWindowType(WindowType.ACTIVITY).forEach {window ->
                llActivity.addView(Button(this).apply { text = "打开 ${window.name}"
                    setOnClickListener { with.jump(window) }  // 注：此处不能使用mWindowInfo获取当前windowInfo对象，因为此处的this指代的是View Button
                },ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    /**
     * 更新未读消息显示
     */
    fun updateUnReadCount(){
        val calcChildUnReadCount = mWindowInfo.calcChildUnReadCount()
        supportActionBar!!.title = if (calcChildUnReadCount != 0) "新消息：$calcChildUnReadCount" else "WindowTree"
        (0 until tabLayout.tabCount).forEach {
            val windowInfo = tabLayout.getTabAt(it)!!.tag as WindowInfo<*>
            val count = windowInfo.calcChildUnReadCount()
            tabLayout.getTabAt(it)!!.text = windowInfo.name + if (count == 0) "" else "($count)"
        }
        (0 until llActivity.childCount).forEach {
            val findChildByIndex = mWindowInfo.findChildByIndex<Any>(it, WindowType.ACTIVITY)!!
            (llActivity.getChildAt(it) as Button).apply {
                text = "打开 ${findChildByIndex.name}"
                val count = findChildByIndex.calcChildUnReadCount()
                if (count != 0){
                    append("($count)")
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun release(){
        WindowTree.destroy()
        llActivity.visibility = View.GONE
        llActivity.removeAllViews()
        tabLayout.removeAllTabs()
        frameLayout.removeAllViews()
        System.gc()
        tv_log.text = ""
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }
}
