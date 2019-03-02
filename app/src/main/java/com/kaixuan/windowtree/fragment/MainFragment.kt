package com.kaixuan.windowtree.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree
import com.kaixuan.windowtreelibrary.mWindowInfo
import kotlinx.android.synthetic.main.fragment_test.*

@Window(parentClass = MainActivity::class,index = 1,name = "主页")
class MainFragment : Fragment() {

    lateinit var with : WindowInfo<String>
    var mView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        mView ?: inflater.inflate(R.layout.fragment_test,container,false).apply { mView = this }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with = WindowTree.instance.with(this)!!
        val t = with.setTag("")
        tv_log.movementMethod = ScrollingMovementMethod.getInstance();
        tv_title.text = "我是" + mWindowInfo!!.getClazz()!!.simpleName
        btn_send.setOnClickListener {
            mWindowInfo.unReadMsgCount ++
            val response = with.sendData("hello,我是${javaClass.simpleName}", with.parent!!)
            tv_log.append("收到了回信：${with.parent!!.getClazz()!!.simpleName}:${response}\n")
        }
        btn_resetUnReadCount.setOnClickListener { mWindowInfo.unReadMsgCount = 0 }

    }


}
