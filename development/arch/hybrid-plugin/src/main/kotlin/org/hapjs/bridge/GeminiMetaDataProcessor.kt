package org.hapjs.bridge

import javassist.CtClass
import org.hapjs.bridge.annotation.*

/**
 * 用于处理feature、widget 、module、inherited注解
 * 新的注解处理不要在这里添加，另起炉灶模拟EventTargetProcessor在创建一个Processor！！！！！！
 */
class GeminiMetaDataProcessor : BaseProcessor<MetaData>() {
    override val modifiedClassName = "org.hapjs.webviewapp.bridge.WebMetaDataSetImpl"
    override fun supportAnnotation() = setOf(WebInheritedAnnotation::class.java,WebFeatureExtensionAnnotation::class.java)
    override fun map(ctClass: CtClass): MetaData? {
        val webInheritedAnnotation =
            ctClass.getAnnotation(WebInheritedAnnotation::class.java) as? WebInheritedAnnotation
        if (webInheritedAnnotation != null) {
            return Inherited(ctClass.name, ctClass.getSuperclasses())
        }
        val webFeatureExtensionAnnotation =
            ctClass.getAnnotation(WebFeatureExtensionAnnotation::class.java) as? WebFeatureExtensionAnnotation
        if (webFeatureExtensionAnnotation != null) {
            return WebFeatureExtension(
                webFeatureExtensionAnnotation.name,
                ctClass.name,
                ctClass.getSuperclasses(),
                webFeatureExtensionAnnotation.getMethods(),
            )
        }
        return null
    }

    override fun process(modifiedCtClass: CtClass, sons: List<MetaData>) {
        //去掉继承链的无效类
        //todo:是否抛出异常让开发者修改会更佳合理而不是remove
        val extensions = sons.filterIsInstance<Extension>()
            .toMutableList<Extension?>()
            .removeParent { item, other ->
                return@removeParent item.name != other.name
            }
        sons.filterIsInstance<Inherited>().forEach { inherited ->
            val sucess = extensions.replace(inherited)
            if (!sucess) {
                throw RuntimeException("Fail to resolve inherited: " + inherited.classname)
            }
        }
        extensions.removeParent { item, other ->
            return@removeParent item.name != other.name
        }
        P.info("web extensions:${extensions.size}个 $extensions")
        modifiedCtClass.injectMetaData(extensions,
            "initFeatureMetaData"
        )

    }
}

data class WebFeatureExtension(
    override val name: String,
    override var classname: String,
    override var superClasses: List<String>,
    override val methods: List<Method>,
) : Extension

fun WebFeatureExtensionAnnotation?.getMethods() = getMethods(this?.actions)