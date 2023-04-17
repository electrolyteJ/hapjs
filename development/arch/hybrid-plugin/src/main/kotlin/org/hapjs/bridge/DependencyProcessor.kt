package org.hapjs.bridge

import javassist.CtClass
import org.hapjs.bridge.annotation.DependencyAnnotation

class DependencyProcessor : BaseProcessor<Dependency>() {
    override val modifiedClassName: String
        get() = "org.hapjs.bridge.DependencyManagerImpl"
    override fun supportAnnotation() = setOf(DependencyAnnotation::class.java)

    override fun map(ctClass: CtClass): Dependency {
        val dependencyAnnotation =
            ctClass.getAnnotation(DependencyAnnotation::class.java) as DependencyAnnotation
        return Dependency(dependencyAnnotation.key, ctClass.name, ctClass.getSuperclasses())
    }

    override fun process(modifiedCtClass: CtClass, sons: List<Dependency>) {
        val deps = sons.toMutableList<Dependency?>().removeParent()
        if (deps.isEmpty()) return
        modifiedCtClass.getDeclaredMethod("initDependencyMetaData").insertAfter(buildString {
            append("org.hapjs.bridge.DependencyManager.Dependency dependency;")
            deps.filterNotNull().forEach { d->
                append("dependency = new org.hapjs.bridge.DependencyManager.Dependency(\"${d.classname}\");")
                append("$1.put(\"${d.key}\",dependency);")
            }
        })
    }


}

data class Dependency(
    val key: String,
    override var classname: String,
    override var superClasses: List<String>,
) : MetaData