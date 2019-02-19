package com.kaixuan.windowtree.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.myWindowInfo
import kotlinx.android.synthetic.main.activity_test.*

@Window(parentClass = MainActivity::class,index = 2,name = "动态")
class DynamicFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.activity_test,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_title.text = "我是" + myWindowInfo()!!.getClazz()!!.simpleName
        btn_send.setOnClickListener {
            myWindowInfo()!!.sendData("hello,我是${javaClass.simpleName}", myWindowInfo()!!.parent!!)
        }
    }
}
