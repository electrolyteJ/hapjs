package org.hapjs.bridge

import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import org.gradle.api.Project

class HybridPlugin : ScanClassPlugin() {
    override fun getName() = "hybridapp"
    override fun isIncremental() = false
    val classPool = ClassPool()
    lateinit var project: Project
    private val processors = listOf<BaseProcessor<*>>(
        QuickAppMetaDataProcessor(), EventTargetProcessor(),
        DependencyProcessor(),
        GeminiMetaDataProcessor()
    )
    var start = -1L

    override fun apply(project: Project) {
        super.apply(project)
        this.project = project
    }

    override fun onScanBegin(transformInvocation: TransformInvocation) {
        ClassPool.cacheOpenedJarFile = false
        project.extensions.findByType(com.android.build.gradle.BaseExtension::class.java)?.bootClasspath?.forEach {
            classPool.appendClassPath(it.absolutePath)
        }
        processors.forEach {
            it.start(project, transformInvocation)
        }
    }

    override fun onScanClass(info: ClassInfo) {
        try {
            if (!info.canonicalName.contains("") && !info.canonicalName.contains("org.hapjs")) return
            info.mather1?.let {
                classPool.appendClassPath(it.absolutePath)
            }
            processors.forEach {
                if (it.modifiedClassName == info.canonicalName) {
                    it.modifiedClassInfo = info
                } else {
                    val ctClass = classPool.getCtClass(info.canonicalName)
                    var used = false
                    for (a in it.supportAnnotation()) {
                        if (ctClass.hasAnnotation(a)) {
                            it.allAnnotatedName.add(ctClass)
                            used = true
                            break
                        }
                    }
//                    if (!used) ctClass.detach()
                }
            }

        } catch (e: Exception) {
            P.error("${info.canonicalName}  $e")
        }
    }

    override fun onScanEnd() {
        for (processor in processors) {
            if (processor.modifiedClassInfo?.classFile == null && processor.modifiedClassInfo?.classStream == null) {
                P.error("[${processor.javaClass.simpleName}] 找不到 ${processor.modifiedClassName}文件")
                continue
            }
            if (processor.allAnnotatedName.isEmpty()) {
                continue
            }
            start = System.currentTimeMillis()
            processor.allAnnotatedName.forEach { ctClass ->
                try {
                    processor.addSon(ctClass)
                } catch (e: Exception) {
                    e.printStackTrace()
                    P.error("[${processor.javaClass.simpleName}] canonicalName:${ctClass.name}")
                    throw e
                } finally {
                    ctClass.detach()//删除缓存
                }
            }
            P.info("[${processor.javaClass.simpleName}] foreach allAnnotatedName cost:${System.currentTimeMillis() - start}ms")
            start = System.currentTimeMillis()
            Injector.injectCode(processor.modifiedClassInfo) { where, inputStream ->
                val modifiedCtClass = classPool.makeClass(inputStream)
                try {
                    modifiedCtClass.defrost()
                    P.info("[${processor.javaClass.simpleName}] ${modifiedCtClass.simpleName} path : ${processor.modifiedClassInfo}")
                    processor.process(modifiedCtClass)
                    modifiedCtClass.toBytecode()
                } catch (e: Exception) {
                    P.error("[${processor.javaClass.simpleName}] could not insert code in ${modifiedCtClass.name};  ${processor.modifiedClassInfo?.mather2?.absolutePath}$e")
                    byteArrayOf()
                } finally {
                    modifiedCtClass.detach()
                    P.info("[${processor.javaClass.simpleName}] inject code to ${modifiedCtClass.simpleName} cost:${System.currentTimeMillis() - start}ms\n")
                }
            }
            processor.end()
        }
    }
}
