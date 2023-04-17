package org.hapjs.bridge

import com.android.build.api.transform.TransformInvocation
import groovy.json.JsonOutput
import javassist.CtClass
import org.gradle.api.Project
import org.hapjs.bridge.annotation.*
import java.io.File


/**
 * 用于处理feature、widget 、module、inherited注解
 * 新的注解处理不要在这里添加，另起炉灶模拟EventTargetProcessor在创建一个Processor！！！！！！
 */
class QuickAppMetaDataProcessor : BaseProcessor<MetaData>() {
    val  closePrintStep = true
    override val modifiedClassName = "org.hapjs.bridge.MetaDataSetImpl"
    lateinit var variantName: String
    lateinit var project: Project
    val cardJsonFile: File by lazy {
        File(project.buildDir, "intermediates/merged_assets/${variantName}/out/hap/card.json")
    }

    override fun supportAnnotation() = setOf(
        InheritedAnnotation::class.java,
        FeatureExtensionAnnotation::class.java,
        ModuleExtensionAnnotation::class.java,
        WidgetExtensionAnnotation::class.java, WidgetAnnotation::class.java,
    )

    override fun start(project: Project, transformInvocation: TransformInvocation) {
        super.start(project, transformInvocation)
        variantName = transformInvocation.context.variantName
        this.project = project
    }

    override fun map(ctClass: CtClass): MetaData? {
        val inheritedAnnotation =
            ctClass.getAnnotation(InheritedAnnotation::class.java) as? InheritedAnnotation
        if (inheritedAnnotation != null) {
            return Inherited(ctClass.name, ctClass.getSuperclasses())
        }
        val featureExtensionAnnotation =
            ctClass.getAnnotation(FeatureExtensionAnnotation::class.java) as? FeatureExtensionAnnotation
        if (featureExtensionAnnotation != null) {
            return FeatureExtension(
                featureExtensionAnnotation.name,
                ctClass.name,
                ctClass.getSuperclasses(),
                featureExtensionAnnotation.getMethods(),
                featureExtensionAnnotation.residentType
            )
        }
        val moduleExtensionAnnotation =
            ctClass.getAnnotation(ModuleExtensionAnnotation::class.java) as? ModuleExtensionAnnotation
        if (moduleExtensionAnnotation != null) {
            return ModuleExtension(
                moduleExtensionAnnotation.name,
                ctClass.name,
                ctClass.getSuperclasses(),
                moduleExtensionAnnotation.getMethods(),
            )
        }
        val widgetExtensionAnnotation =
            ctClass.getAnnotation(WidgetExtensionAnnotation::class.java) as? WidgetExtensionAnnotation
        if (widgetExtensionAnnotation != null) {
            return WidgetExtension(
                widgetExtensionAnnotation.name,
                ctClass.name,
                ctClass.getSuperclasses(),
                widgetExtensionAnnotation.getMethods(),
            )
        }
        val widgetAnnotation =
            ctClass.getAnnotation(WidgetAnnotation::class.java) as? WidgetAnnotation
        if (widgetAnnotation != null) {
            return Widget(
                widgetAnnotation.name,
                ctClass.name,
                ctClass.getSuperclasses(),
                widgetAnnotation.needDeleteSuperClasses,
                widgetAnnotation.methods.toList(),
                widgetAnnotation.getTypes()
            )
        }
        return null
    }

    override fun process(modifiedCtClass: CtClass, sons: List<MetaData>) {
        val extensions = sons.filterIsInstance<Extension>().toMutableList<Extension?>()
        if (extensions.isEmpty()) return
        val widgetMutableList = sons.filterIsInstance<Widget?>().toMutableList()
        if (widgetMutableList.isEmpty()) return
        sons.filterIsInstance<Inherited>().forEach { inherited ->
            var sucess = extensions.replace(inherited)
            if (!sucess) {
                sucess = widgetMutableList.replace(inherited)
            }
            if (!sucess) {
                throw RuntimeException("Fail to resolve inherited: " + inherited.classname)
            }
        }
        //todo:是否抛出异常让开发者修改会更佳合理而不是remove
        extensions.removeParent { item, other ->
            return@removeParent item.name != other.name
        }
        widgetMutableList.remove(skip = { item, other ->
            item.name != other.name
        }, deleteCondition = l@{ i, item, j, other ->
            if (item.superClasses.contains(other.classname) && item.needDeleteSuperClasses) {
                return@l j
            } else if (other.superClasses.contains(item.classname) && other.needDeleteSuperClasses) {
                return@l i
            }
            -1
        })
        val widgetList = widgetMutableList.filterNotNull().toList()
        val groupBy = extensions.filterNotNull().groupBy { it::class.java }
        //1.将extensions widget list数据插入initXxxMetaData
        modifiedCtClass.injectMetaData(groupBy[FeatureExtension::class.java], "initFeatureMetaData")
        modifiedCtClass.injectMetaData(groupBy[ModuleExtension::class.java], "initModuleMetaData")
        modifiedCtClass.injectMetaData(groupBy[WidgetExtension::class.java], "initWidgetMetaData")
        modifiedCtClass.injectMetaData(widgetList, "initWidgetList")
        printlnStep1(
            groupBy[FeatureExtension::class.java],
            groupBy[ModuleExtension::class.java],
            groupBy[WidgetExtension::class.java],
            widgetList
        )
        //2.将extensions widget list数据jsonify 之后插入到getXxxMetaDataJSONString,getXxxMetaDataJSONString函数用来注入到js引擎提供给前端调用
        val extensionGroup = extensions.filterNotNull().groupingBy { it::class.java }
            .fold(initialValue = listOf<Map<String, Any>>()) { accumulator, element: Extension ->
                //将源数据流按照FeatureExtension ModuleExtension WidgetExtension分组以后，再将每个分组的数据jsonify，
                //姑且将Map<String, Any>看成是java json对象 ，最后会被转换成json字符串
                val instance = ExtensionMetaData(element.name, element.classname)
                for (method in element.methods) {
                    instance.addMethod(
                        method.name, method.isInstanceMethod, method.mode.name,
                        method.type.name, method.access.name, method.normalize.name,
                        method.multiple.name, method.alias, method.permissions, method.subAttrs
                    )
                }
                instance.validate()
                return@fold accumulator + instance.toJSON()
            }
        //feature extension
        extensionGroup[FeatureExtension::class.java]?.let {
            val qaJsonString = JsonOutput.toJson(it)
//            val cardJsonString =
//                cardJsonFile.getCardJsonString("featureBlacklist", it) ?: qaJsonString
            modifiedCtClass.injectMetaDataJSONString(
                "getFeatureMetaDataJSONString",
                qaJsonString,
                qaJsonString
            )
            printlnStep2("getFeatureMetaDataJSONString", qaJsonString, qaJsonString)
        }

        //module extension
        extensionGroup[ModuleExtension::class.java]?.let {
            val qaJsonString = JsonOutput.toJson(it)
            modifiedCtClass.injectMetaDataJSONString(
                "getModuleMetaDataJSONString",
                qaJsonString,
                qaJsonString
            )
            printlnStep2("getModuleMetaDataJSONString", qaJsonString, qaJsonString)
        }
        //widget extension
        extensionGroup[WidgetExtension::class.java]?.let {
            val qaJsonString = JsonOutput.toJson(it)
//            val cardJsonString =
//                cardJsonFile.getCardJsonString("componentBlacklist", it) ?: qaJsonString
            modifiedCtClass.injectMetaDataJSONString(
                "getWidgetMetaDataJSONString",
                qaJsonString,
                qaJsonString
            )
            printlnStep2("getWidgetMetaDataJSONString", qaJsonString, qaJsonString)
        }
        //widget component
        widgetList.map { it.toJSON() }.toMutableList().let {
            val qaJsonString = JsonOutput.toJson(it)
//            val cardJsonString =
//                cardJsonFile.getCardJsonString("componentBlacklist", it) ?: qaJsonString
            modifiedCtClass.injectMetaDataJSONString(
                "getWidgetListJSONString",
                qaJsonString,
                qaJsonString
            )
            printlnStep2("getWidgetListJSONString", qaJsonString, qaJsonString)
        }

        //3.feature extension中有些接口具备resident功能，分装捡练出来USEABLE、RESIDENT_NORMAL、RESIDENT_IMPORTANT
        val residentGroup =
            groupBy[FeatureExtension::class.java]?.filterIsInstance<FeatureExtension>()
                ?.groupingBy { it.residentType }?.fold(String()) { accumulator, extension ->
                    accumulator + "$1.add(\"${extension.name}\");"
                }
        modifiedCtClass.injectResidentSet(
            residentGroup?.get(FeatureExtensionAnnotation.ResidentType.USEABLE)?.toString(),
            "initResidentWhiteSet"
        )
        modifiedCtClass.injectResidentSet(
            residentGroup?.get(FeatureExtensionAnnotation.ResidentType.RESIDENT_NORMAL)
                ?.toString(),
            "initResidentNormalSet"
        )
        modifiedCtClass.injectResidentSet(
            residentGroup?.get(FeatureExtensionAnnotation.ResidentType.RESIDENT_IMPORTANT)
                ?.toString(),
            "initResidentImportantSet"
        )
        modifiedCtClass.injectResidentSet(buildString {
            groupBy[FeatureExtension::class.java]?.filterIsInstance<FeatureExtension>()
                ?.forEach { extension ->
                    extension.methods.filter { it.residentType != ActionAnnotation.ResidentType.NONE }
                        .forEach { method ->
                            append("$1.add(\"${extension.name}_${method.name}\");")
                        }
                }
        }, "initMethodResidentWhiteSet")

        printlnStep3(groupBy[FeatureExtension::class.java])
    }

    private fun printlnStep1(
        features: List<Extension>?,
        modules: List<Extension>?,
        widgets: List<Extension>?,
        widgetList: List<Widget>?
    ) {
        if (closePrintStep) return
        P.info(
            "1. extensions:${(features?.size ?: 0) + (modules?.size ?: 0) + (widgets?.size ?: 0)}个,widget list:${widgetList?.size}个\n " +
                    "feature extension:${features}\n " +
                    "module extension:${modules}\n " +
                    "widget extension:${widgets}\n" +
                    "widget list:$widgetList"
        )
    }

    fun printlnStep2(methodName: String, cardJsonString: String, qaJsonString: String) {
        if (closePrintStep) return
        P.info(
            "2. $methodName 卡片与快应用的jsonstring 是否相等？ ${cardJsonString == qaJsonString}\n" +
                    "cardJsonString:$cardJsonString\n" +
                    "qaJsonString:$qaJsonString"
        )
    }
    @OptIn(ExperimentalStdlibApi::class)
    private fun printlnStep3(features: List<Extension>?) {
        if (closePrintStep) return
        if (features.isNullOrEmpty()) return
        val residentGroup =
            features.filterIsInstance<FeatureExtension>().groupBy { it.residentType }
        val useableResidentGroup = residentGroup[FeatureExtensionAnnotation.ResidentType.USEABLE]
        val normalResidentGroup =
            residentGroup[FeatureExtensionAnnotation.ResidentType.RESIDENT_NORMAL]
        val importantResidentGroup =
            residentGroup[FeatureExtensionAnnotation.ResidentType.RESIDENT_IMPORTANT]
        val a = buildList {
            features.filterIsInstance<FeatureExtension>()
                .forEach { extension ->
                    extension.methods.filter { it.residentType != ActionAnnotation.ResidentType.NONE }
                        .forEach { method ->
                            add("${extension.name}_${method.name}")
                        }
                }
        }
        P.info(
            "3. useable resident:${useableResidentGroup?.size}个 ${useableResidentGroup}\n " +
                    "normal resident:${normalResidentGroup?.size}个 ${normalResidentGroup}\n " +
                    "important resident:${importantResidentGroup?.size}个 $importantResidentGroup\n" +
                    "method resident:${a.size}个 $a"
        )
    }


}

/**
 * 被InheritedAnnotation注解的类，需要从List中找到其父类的MetaData,并且修改实例化的类
 * @return: false 修改失败，true 修改成功
 */
inline fun <reified T : MetaData> MutableList<T?>?.replace(i: Inherited): Boolean {
    if (this.isNullOrEmpty()) return false
    for (superClass in i.superClasses) {
        for (e in this) {
            e ?: continue
            if (e.classname == superClass) {
                e.classname = i.classname
                e.superClasses = i.superClasses
                if (e is Widget) e.needDeleteSuperClasses = true
                return true
            }
        }
    }
    return false
}

data class Inherited(
    override var classname: String,
    override var superClasses: List<String>
) : MetaData

data class Widget(
    val name: String,
    override var classname: String,
    override var superClasses: List<String>,
    var needDeleteSuperClasses: Boolean,
    val methods: List<String>,
    val types: List<Type>,
) : MetaData {
    class Type(val name: String, val isDefault: Boolean)

    fun toJSON(): Map<String, Any> {
        val json = mutableMapOf<String, Any>()
        json["name"] = name
        if (types.isNotEmpty()) {
            val typesJson = mutableListOf<String>()
            val iterator = types.iterator()
            while (iterator.hasNext()) {
                typesJson.add(iterator.next().name)
            }
            json["types"] = typesJson
        }
        if (methods.isNotEmpty()) {
            json["methods"] = methods
        }
        return json
    }
}

data class FeatureExtension(
    override val name: String,
    override var classname: String,
    override var superClasses: List<String>,
    override val methods: List<Method>,
    val residentType: FeatureExtensionAnnotation.ResidentType,
) : Extension

data class WidgetExtension(
    override val name: String,
    override var classname: String,
    override var superClasses: List<String>,
    override val methods: List<Method>,
) : Extension

data class ModuleExtension(
    override val name: String,
    override var classname: String,
    override var superClasses: List<String>,
    override val methods: List<Method>,
) : Extension


fun FeatureExtensionAnnotation?.getMethods() = getMethods(this?.actions)
fun WidgetExtensionAnnotation?.getMethods() = getMethods(this?.actions)
fun ModuleExtensionAnnotation?.getMethods() = getMethods(this?.actions)
fun WidgetAnnotation?.getTypes(): List<Widget.Type> {
    val l = mutableListOf<Widget.Type>()
    if (this?.types.isNullOrEmpty()) return l
    for (type in this!!.types) {
        l.add(Widget.Type(type.name, type.isDefault))
    }
    return l
}

