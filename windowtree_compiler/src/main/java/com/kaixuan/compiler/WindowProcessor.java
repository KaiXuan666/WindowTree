package com.kaixuan.compiler;


import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.kaixuan.windowtree_annotation.enums.WindowType;
import com.kaixuan.windowtree_annotation.model.WindowInfo;
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
    /**
     * 日志相关的辅助类
     */
    private Types types;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
        mMessager.printMessage(Diagnostic.Kind.WARNING, "init : ");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, List<WindowInfo>> stringListMap = parseWindows(roundEnvironment.getElementsAnnotatedWith(Window.class));
        List<String> main = new ArrayList<>();


        for (String s : stringListMap.keySet()) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder("loadWindowTree")//定义方面名
                    .addModifiers(Modifier.PUBLIC)//定义修饰符
                    .addAnnotation(Override.class)
                    .returns(void.class)//定义返回结果
                    .addParameter(WindowInfo.class, "currentWindowMeta");//添加方法参数
            List<WindowInfo> windowInfos = stringListMap.get(s);
            windowInfos.sort(new Comparator<WindowInfo>() {
                @Override
                public int compare(WindowInfo windowMeta, WindowInfo t1) {
                    return windowMeta.index - t1.index;
                }
            });
            mMessager.printMessage(Diagnostic.Kind.WARNING, "windowInfos windowInfos : " +windowInfos.toString());
            for (WindowInfo meta : windowInfos) {
                mMessager.printMessage(Diagnostic.Kind.WARNING, "windowInfos meta.index : " + meta.index);
                builder.addStatement("currentWindowMeta.addChild($S,$S,$L,$L)", meta.getClazzName(),meta.name,meta.index,meta.getWindowType());//添加方法内容
            }
            MethodSpec methodSpec = builder.addException(ClassName.get(ClassNotFoundException.class))
                    .build();
            String tempClass = s.substring(s.lastIndexOf(".") + 1) + "$Gen";
            TypeSpec finderClass = TypeSpec.classBuilder(tempClass)
                    .addSuperinterface(ClassName.get(mElementUtils.getTypeElement("com.kaixuan.windowtreelibrary.template.IWindowTreeLoad")))
                    .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJECTOR, TypeName.get(mClassElement.asType())))
                    .addMethod(methodSpec)
                    .build();
            // 创建Java文件
            main.add("com.kaixuan.windowtree.windows." + tempClass );
            JavaFile javaFile = JavaFile.builder("com.kaixuan.windowtree.windows", finderClass)
                    .addStaticImport(WindowType.class, "*").build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ParameterizedTypeName map = ParameterizedTypeName.get(ClassName.get(Map.class)
                , ClassName.get(String.class)
                , ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement("com.kaixuan.windowtreelibrary.template.IWindowTreeLoad")))
                ));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getAllGeneratedFile")
                .addModifiers(Modifier.PUBLIC)//定义修饰符
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(Map.class)
                        , ClassName.get(String.class)
                        , ParameterizedTypeName.get(
                                ClassName.get(Class.class),
                                WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement("com.kaixuan.windowtreelibrary.template.IWindowTreeLoad")))
                        )));//定义返回结果

        ClassName mapNew = ClassName.get("java.util", "HashMap");

        builder.addStatement("$T map = new $T()",map,mapNew);
        for (String s : main) {
            builder.addStatement("map.put($S,$L.class)",s,s);
        }
        builder.addStatement("return map");

        TypeSpec mainClass = TypeSpec.classBuilder(  "Main$Gen")
                .addSuperinterface(ClassName.get(mElementUtils.getTypeElement("com.kaixuan.windowtreelibrary.template.IMain")))
                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJECTOR, TypeName.get(mClassElement.asType())))
                .addMethod(builder.build())
                .build();
        JavaFile javaFile = JavaFile.builder("com.kaixuan.windowtree.windows", mainClass).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Map<String, List<WindowInfo>> parseWindows(Set<? extends Element> routeElements) {
        // 以父节点类名为key平铺存储所有WindowMeta
        Map<String, List<WindowInfo>> map = new HashMap<>();

        TypeMirror type_Activity = mElementUtils.getTypeElement(WindowType.ACTIVITY.getClassName()).asType();
        TypeMirror type_Fragment = mElementUtils.getTypeElement(WindowType.FRAGMENT.getClassName()).asType();
        TypeMirror type_Fragmentv4 = mElementUtils.getTypeElement(WindowType.FRAGMENTV4.getClassName()).asType();
        TypeMirror type_View = mElementUtils.getTypeElement(WindowType.VIEW.getClassName()).asType();
        TypeMirror type_Dialog = mElementUtils.getTypeElement(WindowType.DIALOG.getClassName()).asType();
        TypeMirror type_PopupWindow = mElementUtils.getTypeElement(WindowType.POPUPWINDOW.getClassName()).asType();

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
                List<WindowInfo> windowInfos = map.get(parentName);
                if (windowInfos == null) {
                    windowInfos = new ArrayList<>();
                    map.put(parentName, windowInfos);
                }

                mMessager.printMessage(Diagnostic.Kind.WARNING, "routeElement : " + routeElement.toString());
//               routeElement.toString()
                WindowInfo windowInfo = new WindowInfo(null, routeElement.toString(), null, annotation.name(), annotation.index());
                windowInfos.add(windowInfo);
                // 判断类型
                TypeMirror tm = routeElement.asType();
                if (types.isSubtype(tm,type_Activity)){
                    windowInfo.setWindowType(WindowType.ACTIVITY);
                }else if (types.isSubtype(tm,type_Fragment)){
                    windowInfo.setWindowType(WindowType.FRAGMENT);
                }else if (types.isSubtype(tm,type_Fragmentv4)){
                    windowInfo.setWindowType(WindowType.FRAGMENTV4);
                }else if (types.isSubtype(tm,type_View)){
                    windowInfo.setWindowType(WindowType.VIEW);
                }else if (types.isSubtype(tm,type_Dialog)){
                    windowInfo.setWindowType(WindowType.DIALOG);
                }else if (types.isSubtype(tm,type_PopupWindow)){
                    windowInfo.setWindowType(WindowType.POPUPWINDOW);
                }else {
                    windowInfo.setWindowType(WindowType.UNKNOWN);
                }
            } else {
                mMessager.printMessage(Diagnostic.Kind.WARNING, "not class : " + routeElement.toString());
            }
        }
        mMessager.printMessage(Diagnostic.Kind.WARNING, "map : " + map.toString());
        return map;
    }
}
