package com.kaixuan.windowtreelibrary.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.kaixuan.windowtree_annotation.enums.WindowType
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree
import com.kaixuan.windowtreelibrary.template.IJumpAdapter
import java.lang.ref.WeakReference

class DefaultJumpAdapter : IJumpAdapter {

    //    val weakHashMapFrag : WeakHashMap<String,Any> by lazy { WeakHashMap<String,Any>() }
    val weakReferenceMap: HashMap<String, WeakReference<Any>> by lazy { HashMap<String, WeakReference<Any>>() }

    fun <T> getCache(className: String, create: () -> T): T {

        val weakReference = weakReferenceMap[className]
        if (weakReference?.get() == null) {
            weakReferenceMap[className] = WeakReference(create()) as WeakReference<Any>
        }
        return weakReferenceMap[className]!!.get() as T
    }

    override fun jump(formContext: Context, to: WindowInfo<*>): Boolean {
        val with = WindowTree.with<Any>(formContext)
            ?: throw RuntimeException("找不到与该FormContext对应的WindowInfo; WindowInfo corresponding to FormContext could not be found")
        when (to.windowType) {
            WindowType.ACTIVITY -> {
                formContext.startActivity(Intent(formContext, to.getClazz()).apply { putExtras(to.bundle) })
                return true
            }
            WindowType.FRAGMENT -> {
                (formContext as Activity).fragmentManager.beginTransaction().replace(with.frameLayoutId,
                    getCache(to.getClazzName()) { to.getClazz()!!.newInstance() } as android.app.Fragment
                ).commit()
                return true
            }
            WindowType.FRAGMENTV4 -> {
                (formContext as FragmentActivity).supportFragmentManager.run {
                    beginTransaction().replace(with.frameLayoutId,
                        getCache(to.getClazzName()) { to.getClazz()!!.newInstance() } as Fragment
                    ).commit()
                }
                return true
            }
            else -> {
                throw RuntimeException("暂时不支持自动跳转至${to.windowType}类型窗口，您可以自己实现IJumpAdapter接口，自定义跳转方法")
            }
        }
        return false
    }

    fun clear() {
        weakReferenceMap.clear()
    }

}