package com.kaixuan.windowtree_annotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Window {

    Class parentClass() default Window.class;

    String parentClassName() default "";

    int index() default 0;
}
