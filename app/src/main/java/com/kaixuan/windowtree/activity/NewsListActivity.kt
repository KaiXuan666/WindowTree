package com.kaixuan.windowtree.activity

import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kaixuan.windowtree.MainActivity
import com.kaixuan.windowtree.R
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.mWindowInfo
import kotlinx.android.synthetic.main.activity_news_list.*

@Window(parentClass = MainActivity::class,name = "新闻列表",index = 1)
class NewsListActivity : BaseActivity() {

    val newsList = 0..200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)
        supportActionBar!!.title = mWindowInfo.name
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(LayoutInflater.from(this@NewsListActivity).inflate(R.layout.support_simple_spinner_dropdown_item,p0,false)){}
            }

            override fun getItemCount(): Int = newsList.count()

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
                (p0.itemView as TextView).let {textView ->
                    textView.text = "Hello，这里是新闻啊啊啊啊啊啊啊啊 ${p1}"
                    textView.setOnClickListener {
                        val childByIndex = mWindowInfo.findChildByIndex<String>(0)!!
                        childByIndex.setTag(textView.text.toString())
                        childByIndex.bundle.putInt("unReadCount",p1)
                        mWindowInfo.jump(childByIndex) }
                }
            }
        }
    }

    override fun onDestroy() {
        mWindowInfo.release()
        super.onDestroy()
    }
}
