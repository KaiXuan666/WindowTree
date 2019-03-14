package com.kaixuan.windowtreelibrary

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import android.widget.PopupWindow
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.adapter.DefaultJumpAdapter
import com.kaixuan.windowtreelibrary.template.IJumpAdapter
import com.kaixuan.windowtreelibrary.template.IMain
import com.kaixuan.windowtreelibrary.template.IWindowTreeLoad
import com.kaixuan.windowtreelibrary.util.Consts
import com.kaixuan.windowtreelibrary.util.DefaultLogger
import java.util.*

class WindowTree {

    lateinit var allGeneratedFile:
            Map<String, Class<out IWindowTreeLoad>>;

    val defaultJumpAdapter: DefaultJumpAdapter by lazy { DefaultJumpAdapter() }
    private var jumpAdapter: IJumpAdapter? = null
    var hasPageAuthorityFun: ((Long) -> Boolean)? = null

    /**
     * 最顶级的WindowInfo
     */
    var windowMeta: WindowInfo<Any>? = null;

    val weakHashMap: WeakHashMap<Any, WindowInfo<*>> = WeakHashMap()

    companion object {
        lateinit var mContext: Context
        @JvmField
        @Volatile
        var debuggable = false
        @Volatile
        var hasInit = false
        @JvmField
        val logger = DefaultLogger(Consts.TAG)
        val instance: WindowTree by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WindowTree() }

        fun init(application: Application) {
            if (!hasInit) {
                hasInit = true
                mContext = application.applicationContext
                logger.showLog(true)
                val newInstance =
                    Class.forName(Consts.GENERATE_FILE_PATH + ".Main" + Consts.GENERATE_FILE_NAME_END).getConstructor()
                        .newInstance()
                instance.allGeneratedFile = (newInstance as IMain).allGeneratedFile
                val rootWindow =
                    instance.allGeneratedFile[Consts.GENERATE_FILE_PATH + ".Window" + Consts.GENERATE_FILE_NAME_END]
                instance.windowMeta = WindowInfo<Any>(
                    Window::class.java,
                    Window::class.java.name,
                    null
                );
                rootWindow!!.getConstructor().newInstance().loadWindowTree(instance.windowMeta)
                bindWindowTree(instance.windowMeta!!)
                logger.info(Consts.TAG, instance.allGeneratedFile.toString())
            }
        }

        fun bindWindowTree(windowMeta: WindowInfo<*>) {
            windowMeta.child.forEach { forItem ->
                val clazz =
                    instance.allGeneratedFile[Consts.GENERATE_FILE_PATH + "." + forItem.getClazz()!!.simpleName + Consts.GENERATE_FILE_NAME_END]
                if (clazz == null) {
                    // 找不到，跳过

                } else {
                    // 找到，继续递归
                    clazz.getConstructor().newInstance().loadWindowTree(forItem)
                    bindWindowTree(forItem)
                }
            }
        }

        fun destroy() {
            if (hasInit) {
                hasInit = false
                instance.windowMeta!!.findWindowInfoByCondition {
                    it.release(true)
                    return@findWindowInfoByCondition false
                }
                instance.weakHashMap.clear()
                instance.windowMeta = null
                instance.hasPageAuthorityFun = null
                instance.defaultJumpAdapter.clear()
                instance.setJumpAdapter(null)
            }
        }

        @JvmStatic
        fun <T> with(obj: Any): WindowInfo<T>? {
            if (!hasInit) {
                throw RuntimeException("WindowTree未初始化")
            }
            return instance.weakHashMap[obj] as WindowInfo<T>?
                ?: instance.windowMeta!!.findWindowInfoByClass(obj.javaClass)?.apply {
                    instance.weakHashMap[obj] = this
                } as WindowInfo<T>?
        }

        @JvmStatic
        fun hasAuthority(pageAuthorityId: Long): Boolean {
            if (!hasInit) {
                throw RuntimeException("WindowTree未初始化")
            }
            return instance.hasPageAuthorityFun?.invoke(pageAuthorityId)
                ?: (pageAuthorityId == -1L) // 不设置权限判断器的情况下，只拥有默认权限的页面
        }
    }

    fun setJumpAdapter(jumpAdapter: IJumpAdapter?) {
        this.jumpAdapter = jumpAdapter
    }

    fun setJumpAdapter(jumpAdapter: (Context, WindowInfo<*>) -> Boolean) {
        this.jumpAdapter = jumpAdapter.toJumpAdapter()
    }

    fun getJumpAdapter(): IJumpAdapter? = this.jumpAdapter

}

/**
 * 简化调用者写法，不需要object :
 */
fun <T : ((Context, WindowInfo<*>) -> Boolean)> T.toJumpAdapter(): IJumpAdapter = object : IJumpAdapter {
    override fun jump(formContext: Context, to: WindowInfo<*>): Boolean = this@toJumpAdapter.invoke(formContext, to)
}

fun getWindowInfo(any: Any): WindowInfo<Any> = WindowTree.with<Any>(any)
    ?: throw RuntimeException("找不到与${any}对应的WindowInfo，请检查。在匿名内部类调用本方法时应谨慎检查this关键字的指代对象。")

val Activity.mWindowInfo: WindowInfo<Any>
    get() = getWindowInfo(this)
val View.mWindowInfo: WindowInfo<Any>
    get() = getWindowInfo(this)
val Fragment.mWindowInfo: WindowInfo<Any>
    get() = getWindowInfo(this)
val android.app.Fragment.mWindowInfo: WindowInfo<Any>
    get() = getWindowInfo(this)
val Dialog.mWindowInfo: WindowInfo<Any>
    get() = getWindowInfo(this)
val PopupWindow.mWindowInfo: WindowInfo<Any>
    get() = getWindowInfo(this)
