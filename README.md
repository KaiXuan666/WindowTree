# WindowTree
只需使用注解，帮助你轻松维护树形界面的层级关系，管理你的界面结构，当你处于界面的任何位置时，都可以知道，我在哪里，我的父界面是谁，我的子界面是谁。甚至能够自动构建你的界面结构。


# WindowTree结构
![avatar](https://img-blog.csdnimg.cn/20190302184300349.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2thaXh1YW5fZGFzaGVu,size_16,color_FFFFFF,t_70)

windowTree将应用内的所有界面都视为一个Window，每个window都拥有与之关联的WindowInfo，借助WindowInfo，你可以完成灵活、强大的工作。

目前支持的功能：

1. 维护了父子界面的层级关系，你在应用的任意位置，都可以知道我的父界面是谁，我的子界面有几个等
2. 灵活的消息通讯，你可以在任意位置发送消息给任意界面，支持双向通讯，你可以知道接收者处理该消息的结果
3. 支持贯穿全局的未读消息小红点，一个父节点能够智能计算出他所拥有的所有子节点的未读消息之和，借助Kotlin的委托属性实现了数量变化实时通知更新！
4. 智能的权限管理，当用户未拥有a的子界面a1，a2的权限时，也将失去a的界面权限
5. 界面自动化搭建，你只需要使用注解定义好界面的层级关系，便可以调用api获取所有子界面，一个循环全部建立
6. 统一管理界面跳转，便于管理界面和埋点统计，减少重复代码
7. 支持Activity、Fragment、View、Dialog、PopupWindow...

<img src="https://img-blog.csdnimg.cn/20190302194910186.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2thaXh1YW5fZGFzaGVu,size_16,color_FFFFFF,t_70" width="400" hegiht="867" align=center />
  
# WindowTree使用


1. 在你应用的所有界面添加注解@Window，参数parentClass指定该界面的父节点（如当前是顶级节点，则不需要设置该属性），可选添加其他属性，index表示当前界面是父界面的第几个同类界面，name表示当前节点名字  


```
@Window(parentClass = MainActivity::class,index = 2,name = "联系人") 
class ContactsFragment : Fragment() 
```  

<br/>
2. 在应用启动时，初始化WindowTree  

<br/>

```
WindowTree.init(MyApp.instance)
``` 

<br/>

3. 
    -  使用kotlin时，可直接在界面类（Activity、Fragment等）中使用扩展属性mWindowInfo拿到当前界面对应的WindowInfo 
    - 使用java时，使用WindowTree.instance.with(this)获取当前界面对应的WindowInfo

<br/>

4. 如需接收消息，需要给当前节点设置setEventListener  

```
mWindowInfo.setEventListener { sender, sendData ->
    if (sendData is UnReadCountEvent){
        tv_log.append("未读消息：${sender.name}的未读消息发生变化，数量变化=${sendData.change}\n")
        updateUnReadCount()
        return@setEventListener "ok 我已收到并处理完毕"  // 支持返回给消息发送者一个回信
    }
    return@setEventListener null 
}
```
windowTree支持消息接收者返回一个结果的应答，告知事件处理结果


<br/>

5. 拿到我的父节点或子节点对象，使用当前的WindowInfo对象可以轻松找到父节点和子节点，如：
```
mWindowInfo.parent
mWindowInfo.child
```

<br/>
6. 界面自动构建，如果你使用TabLayout管理你的界面，只需要以下代码即可将当前界面的所有子界面加入到Tab  


```
tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        mWindowInfo.jump(tab!!.position,WindowType.FRAGMENTV4)
    }
})
mWindowInfo.filterChildByWindowType(WindowType.FRAGMENTV4).forEach { window ->
    tabLayout.addTab(tabLayout.newTab().setText(window.name).setTag(window))
}
```


<br/>
7. 界面跳转控制


```
mWindowInfo.jump(0,WindowType.FRAGMENTV4)
```
这段代码表示，跳转到我的第1个Fragment类型的子界面


DefaultJumpAdapter中默认实现了一些跳转逻辑，你可以继承它实现自己的特殊逻辑

<br/>
<br/>

如果你有更好的建议欢迎与我联系！thank you！kaixuanapp@163.com

github：https://github.com/KaiXuan666/WindowTree
csdn：https://blog.csdn.net/kaixuan_dashen

