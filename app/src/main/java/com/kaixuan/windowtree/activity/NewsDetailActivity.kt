package com.kaixuan.windowtree.activity

import android.os.Bundle
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.mWindowInfo
import com.kaixuan.windowtreelibrary.model.UnReadCountEvent
import kotlinx.android.synthetic.main.activity_news_detail.*

@Window(parentClass = NewsListActivity::class,name = "新闻详情",index = 3)
class NewsDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        supportActionBar!!.title = mWindowInfo.name + mWindowInfo.getTag()
        tv_title.text = mWindowInfo.getTag() as String
        mWindowInfo.unReadMsgCount = mWindowInfo.bundle.getInt("unReadCount")
        tv_unReadCount.text = "未读数量：${mWindowInfo.unReadMsgCount}"
        mWindowInfo.setEventListener { sender, sendData ->
            if (sendData is UnReadCountEvent){
                tv_unReadCount.text = "未读数量：${mWindowInfo.unReadMsgCount}"
            }
        }
        btn_unReadAdd.setOnClickListener { mWindowInfo.unReadMsgCount ++ }
        btn_dealUnRead.setOnClickListener { mWindowInfo.unReadMsgCount = 0 }
    }

    override fun onDestroy() {
        mWindowInfo.release()
        super.onDestroy()
    }

}
