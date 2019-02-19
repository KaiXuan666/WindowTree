package com.kaixuan.windowtree.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.MyApp
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree
import com.kaixuan.windowtreelibrary.myWindowInfo
import kotlinx.android.synthetic.main.activity_test.*

@Window(parentClass = MainActivity::class,index = 1,name = "主页")
class MainFragment : Fragment() {

    lateinit var with : WindowInfo<*>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.activity_test,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with = WindowTree.instance.with(this)!!
        tv_title.text = "我是" + myWindowInfo()!!.getClazz()!!.simpleName
        btn_send.setOnClickListener {
            with.sendData("hello,我是${javaClass.simpleName}", with.parent!!)
        }
//        btnCreateLayout.setOnClickListener {
//            with.sendData("请开始布局", with.parent!!)
//        }
//        btnDestroy.setOnClickListener {
//            with.sendData(0, with.parent!!)
//        }
    }


}
