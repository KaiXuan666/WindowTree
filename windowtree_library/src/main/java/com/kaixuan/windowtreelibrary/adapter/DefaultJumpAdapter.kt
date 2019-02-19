package com.kaixuan.windowtreelibrary.adapter

import android.app.Activity
import android.support.v4.app.Fragment
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.kaixuan.windowtree_annotation.enums.WindowType
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree
import com.kaixuan.windowtreelibrary.template.IJumpAdapter
import java.lang.RuntimeException
import java.util.*

class DefaultJumpAdapter : IJumpAdapter {

    val weakHashMapFrag : WeakHashMap<String,Any> by lazy { WeakHashMap<String,Any>() }

    override fun jump(formContext: Context, to: WindowInfo<*>) {
        val with = WindowTree.instance.with(formContext)
            ?: throw RuntimeException("找不到与该FormContext对应的WindowInfo; WindowInfo corresponding to FormContext could not be found")
        when(to.windowType){
            WindowType.ACTIVITY -> {
                formContext.startActivity(Intent(formContext,to.getClazz()).apply { putExtras(to.bundle) })
            }
            WindowType.FRAGMENT -> {
                val frag = weakHashMapFrag[to.getClazzName()] ?: to.getClazz()!!.newInstance() as android.app.Fragment
                weakHashMapFrag[to.getClazzName()] = frag
                (formContext as Activity).fragmentManager.beginTransaction().replace(with.frameLayoutId,
                    frag as android.app.Fragment?
                ).commit()
            }
            WindowType.FRAGMENTV4 -> {
                val frag = weakHashMapFrag[to.getClazzName()] ?: to.getClazz()!!.newInstance() as Fragment
                weakHashMapFrag[to.getClazzName()] = frag
                (formContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(with.frameLayoutId,
                    frag as Fragment
                ).commit()
            }
            else -> throw RuntimeException("暂时不支持自动跳转至${to.windowType}类型窗口")
        }
    }

}