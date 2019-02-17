package com.kaixuan.compiler;


import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.kaixuan.windowtree_annotation.model.WindowMeta;
import com.kaixuan.windowtree_annotation.annotation.Window;
import com.squareup.javapoet.*;

import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.kaixuan.windowtree_annotation.annotation.Window")
public class WindowProcessor extends AbstractProcessor {

    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
        mMessager.printMessage(Diagnostic.Kind.WARNING, "init : ");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, List<WindowMeta>> stringListMap = parseWindows(roundEnvironment.getElementsAnnotatedWith(Window.class));

        for (String s : stringListMap.keySet()) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder("loadWindowTree")//定义方面名
                    .addModifiers(Modifier.PUBLIC)//定义修饰符
                    .addAnnotation(Override.class)
                    .returns(void.class)//定义返回结果
                    .addParameter(WindowMeta.class, "currentWindowMeta");//添加方法参数
            List<WindowMeta> windowMetas = stringListMap.get(s);
            windowMetas.sort(new Comparator<WindowMeta>() {
                @Override
                public int compare(WindowMeta windowMeta, WindowMeta t1) {
                    return windowMeta.index - t1.index;
                }
            });
            for (WindowMeta meta : windowMetas) {
                builder.addStatement("currentWindowMeta.addChild($S)", meta.getClazzName());//添加方法内容
            }
            MethodSpec methodSpec = builder.addException(ClassName.get(ClassNotFoundException.class))
                    .build();
            TypeSpec finderClass = TypeSpec.classBuilder(s.substring(s.lastIndexOf(".") + 1) + "$Gen")
                    .addSuperinterface(ClassName.get(mElementUtils.getTypeElement("com.kaixuan.windowtreelibrary.template.IWindowTreeLoad")))
                    .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJECTOR, TypeName.get(mClassElement.asType())))
                    .addMethod(methodSpec)
                    .build();
            // 创建Java文件
            JavaFile javaFile = JavaFile.builder("com.kaixuan.windowtree.windows", finderClass).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private Map<String, List<WindowMeta>> parseWindows(Set<? extends Element> routeElements) {
        // 以父节点类名为key平铺存储所有WindowMeta
        Map<String, List<WindowMeta>> map = new HashMap<>();

        for (Element routeElement : routeElements) {
            Window annotation = routeElement.getAnnotation(Window.class);
            if (routeElement.getKind() == ElementKind.CLASS) {
                String parentName = annotation.parentClassName();
                if (parentName.isEmpty()) {
                    try {
                        mMessager.printMessage(Diagnostic.Kind.WARNING, "parentClass : " + annotation.parentClass());
                    } catch (MirroredTypeException e) {
                        parentName = e.getTypeMirror().toString();
//                       mMessager.printMessage(Diagnostic.Kind.WARNING,"e.getTypeMirror() : " + e.getTypeMirror().toString());
                    }
                }
                List<WindowMeta> windowMetas = map.get(parentName);
                if (windowMetas == null) {
                    windowMetas = new ArrayList<>();
                    map.put(parentName, windowMetas);
                }

                mMessager.printMessage(Diagnostic.Kind.WARNING, "routeElement : " + routeElement.toString());
//               routeElement.toString()
                windowMetas.add(new WindowMeta(null, routeElement.toString(), null));
            } else {
                mMessager.printMessage(Diagnostic.Kind.WARNING, "not class : " + routeElement.toString());
            }
        }
        mMessager.printMessage(Diagnostic.Kind.WARNING, "map : " + map.toString());
        return map;
    }
}
