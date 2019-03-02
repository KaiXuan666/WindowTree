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
import com.kaixuan.windowtreelibrary.myWindowInfo
import kotlinx.android.synthetic.main.fragment_test.*

@Window(parentClass = MainActivity::class,index = 3,name = "Vip隐藏界面",pageAuthority = 1)
class VipFragment : Fragment() {

    var mView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        mView ?: inflater.inflate(R.layout.fragment_test,container,false).apply { mView = this }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_log.movementMethod = ScrollingMovementMethod.getInstance();
        tv_title.text = "我是Vip界面，一般人没有权限打开我"
        btn_send.setOnClickListener {
            val response = myWindowInfo().sendData("hello,我是${javaClass.simpleName},pageAuthority=${myWindowInfo().pageAuthority}", myWindowInfo().parent!!)
            tv_log.append( "收到了回信：${myWindowInfo().parent!!.getClazz()!!.simpleName}:$response\n")
        }
    }
}
