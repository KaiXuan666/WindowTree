package com.kaixuan.windowtree_annotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Window {

    /**
     *
     * @return 父class
     */
    Class parentClass() default Window.class;

    /**
     *
     * @return 父class完整类名，该属性和parentClass只需要任选实现一个即可，该属性便于模块化场景下无法直接引用目标类的情况
     */
    String parentClassName() default "";

    /**
     * 功能名称 可空
     * @return
     */
    String name() default "";

    /**
     * 该窗口是父节点的第几个子窗口
     * @return
     */
    int index() default 0;

    /**
     * 该窗口在你的项目中，页面权限id是多少
     * 该值默认为-1时，即该页面无权限控制，任何人都允许加载
     * @return
     */
    int pageAuthority() default -1;
}
