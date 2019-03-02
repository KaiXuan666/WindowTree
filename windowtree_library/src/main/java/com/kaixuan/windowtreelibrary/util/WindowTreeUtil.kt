package com.kaixuan.windowtreelibrary.util

import android.content.Context
import com.kaixuan.windowtreelibrary.WindowInfo
import com.kaixuan.windowtreelibrary.WindowTree

class WindowTreeUtil{

    companion object {

        fun <K,V> findKeyByValue(map: Map<K,V>,value : V): K? {
            return map.entries.find { it.value == value }?.key
        }

        fun findContextByInfo(windowInfo: WindowInfo<*>): Context? {
            val findKeyByValue = findKeyByValue(WindowTree.instance.weakHashMap, windowInfo) ?: return null
            return getContext(findKeyByValue)
        }

        fun getContext(any: Any): Context? {
            when(any){
                is android.app.Activity -> return any
                is android.app.Fragment -> return any.activity
                is android.support.v4.app.Fragment -> return any.context
                is android.view.View -> return any.context
                is android.app.Dialog -> return any.context
                is android.widget.PopupWindow -> return any.contentView.context
                else -> return null
            }
        }
    }
}