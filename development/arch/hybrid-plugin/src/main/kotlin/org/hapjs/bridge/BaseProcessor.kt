package org.hapjs.bridge

import com.android.build.api.transform.TransformInvocation
import javassist.CtClass
import org.gradle.api.Project

/**
 * 流式处理：disk的zip(ClassInfo) ---> javassist classpool(CtClass)
 */
abstract class BaseProcessor<T> {
    /**
     * modifiedClassName想要插桩的文件
     */
    abstract val modifiedClassName: String
    var modifiedClassInfo: ClassInfo? = null

    /**
     * 所有被某个注解声明的类
     */
    val allAnnotatedName = mutableSetOf<CtClass>()

    /**
     * map CtClass to T 的集合
     */
    val sons = mutableListOf<T>()

    open fun start(project: Project, transformInvocation: TransformInvocation){

    }

    abstract fun supportAnnotation(): Set<Class<*>>
    fun addSon(ctClass: CtClass) {
        map(ctClass)?.let {
            sons.add(it)
        }
    }

    abstract fun map(ctClass: CtClass): T?
    fun process(modifiedClass: CtClass) = process(modifiedClass, sons)

    /**
     * 将收集到的sons信息注入到modifiedCtClass类中，实现完美插桩
     */
    abstract fun process(modifiedCtClass: CtClass, sons: List<T>)

    open fun end(){

    }
}