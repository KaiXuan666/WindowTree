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
import kotlinx.android.synthetic.main.fragment_test.*

@Window(parentClass = MainActivity::class,index = 3,name = "联系人")
class ContactsFragment : Fragment() {

    var mView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        mView ?: inflater.inflate(R.layout.fragment_test,container,false).apply { mView = this }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_title.text = "我是" + myWindowInfo()!!.getClazz()!!.simpleName
        btn_send.setOnClickListener {
            val response = myWindowInfo()!!.sendData("hello,我是${javaClass.simpleName}", myWindowInfo()!!.parent!!)
            tv_log.append("收到了回信：${myWindowInfo()!!.parent!!.getClazz()!!.simpleName}:$response\n")
        }
    }
}
