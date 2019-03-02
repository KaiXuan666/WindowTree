package com.kaixuan.windowtree.fragment.dynamic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaixuan.windowtree.R
import com.kaixuan.windowtreelibrary.myWindowInfo
import kotlinx.android.synthetic.main.fragment_msg.*

class GoodFriendDynamicFragment : Fragment() {
    var mView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        mView ?: inflater.inflate(R.layout.fragment_msg,container,false).apply { mView = this }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateMsgCount()
        myWindowInfo().setEventListener { sender, sendData ->
            myWindowInfo().unReadMsgCount++
            updateMsgCount()
        }
    }

    fun updateMsgCount(){
        tv_msg_tips.text = "我有${myWindowInfo().unReadMsgCount}条未读消息"
    }

}