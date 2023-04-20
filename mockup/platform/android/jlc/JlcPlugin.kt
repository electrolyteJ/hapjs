package com.nearme.instant.jlc

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.nearme.instant.ClassInfo
import com.nearme.instant.Injector
import com.nearme.instant.P
import com.nearme.instant.ScanClassPlugin
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project
import kotlin.system.exitProcess

data class JobInfo(
    val canonicalName: String,
    val initPhase: Job.InitPhase
)

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class Job(
    val initPhase: InitPhase = InitPhase.onCreate,
    val initProcess: InitProcess = InitProcess.Main,
    val initPriority: Priority = Priority.Normal
) {
    enum class InitPhase {
        attachBaseContext, onCreate, onIdle
    }

    enum class InitProcess {
        All, Main, Launcher, Other
    }

    enum class Priority {
        High, Normal, Low
    }
}

class JlcPlugin : ScanClassPlugin() {
    var jlcAppDelegateClassInfo: ClassInfo? = null
    lateinit var allClassName: MutableList<String>
    lateinit var classPool: ClassPool
    lateinit var project: Project
    override fun isIncremental() = false
    override fun getName() = "jlc"
    override fun apply(project: Project) {
        super.apply(project)
        this.project = project
    }

    var start = -1L
    override fun onScanBegin(transformInvocation: TransformInvocation) {
        classPool = ClassPool()
        ClassPool.cacheOpenedJarFile = false
        project.extensions.findByType(BaseExtension::class.java)?.bootClasspath?.forEach {
            classPool.appendClassPath(it.absolutePath)
        }
        allClassName = mutableListOf()
    }

    override fun onScanClass(info: ClassInfo) {
        try {
            if (!info.canonicalName.contains("com.nearme.instant") && !info.canonicalName.contains("org.hapjs")) return
            info.mather1?.let {
                classPool.appendClassPath(it.absolutePath)
            }
            if (info.canonicalName == "com.nearme.instant.jlc.JlcAppDelegate") {
                jlcAppDelegateClassInfo = info
            } else if (info.canonicalName.endsWith("Job")) {
                allClassName.add(info.canonicalName)
            }
        } catch (e: Exception) {
            P.error("${info.canonicalName}  $e")
        }
    }

    private fun genInsertCode(jobInfo: JobInfo): String {
        return when (jobInfo.initPhase) {
            Job.InitPhase.attachBaseContext -> {
                "com.nearme.instant.jlc.JobRegistry.addOnAttachContextJob(new ${jobInfo.canonicalName}($2, $1));"
            }
            Job.InitPhase.onCreate -> {
                "com.nearme.instant.jlc.JobRegistry.addOnCreateJob(new ${jobInfo.canonicalName}($2, $1));"
            }
            Job.InitPhase.onIdle -> {
                "com.nearme.instant.jlc.JobRegistry.addOnIdleJob(new ${jobInfo.canonicalName}($2, $1));"
            }
        }
    }

    override fun onScanEnd() {
        if (jlcAppDelegateClassInfo?.classFile == null && jlcAppDelegateClassInfo?.classStream == null) {
//            throw IllegalArgumentException("找不到 JlcAppDelegate文件")
            P.error("找不到 JlcAppDelegate文件")
            exitProcess(1)
        }

        val mainProcJobChain = mutableListOf<JobInfo>()
        val launcherProcJobChain = mutableListOf<JobInfo>()
        val otherProcJobChain = mutableListOf<JobInfo>()
        val allProcJobChain = mutableListOf<JobInfo>()
        start = System.currentTimeMillis()
        allClassName.forEach { canonicalName ->
            val ctClass = classPool.getCtClass(canonicalName)
            try {
                if (ctClass.hasAnnotation(Job::class.java)) {
                    val jobAnnotation = ctClass.getAnnotation(Job::class.java) as Job
                    when (jobAnnotation.initProcess) {
                        Job.InitProcess.Main -> {
                            mainProcJobChain.add(JobInfo(ctClass.name, jobAnnotation.initPhase))
                        }
                        Job.InitProcess.Launcher -> {
                            launcherProcJobChain.add(
                                JobInfo(
                                    ctClass.name,
                                    jobAnnotation.initPhase
                                )
                            )
                        }
                        Job.InitProcess.Other -> {
                            otherProcJobChain.add(JobInfo(ctClass.name, jobAnnotation.initPhase))
                        }
                        Job.InitProcess.All -> {
                            allProcJobChain.add(JobInfo(ctClass.name, jobAnnotation.initPhase))
                        }
                    }
                }
                } catch (e: Exception) {
                    e.printStackTrace()
                    P.error("canonicalName:${canonicalName}")
                } finally {
                    ctClass.detach();//删除缓存
                }

        }
        P.info("foreach allClassName cost:${System.currentTimeMillis() - start}ms")
        P.info(
            "JlcAppDelegate path : $jlcAppDelegateClassInfo \n" +
                    ">>>>>>mainProcJobChain：${mainProcJobChain} \n" +
                    ">>>>>>launcherProcJobChain: $launcherProcJobChain \n" +
                    ">>>>>>launcherProcJobChain: $otherProcJobChain \n" +
                    ">>>>>>allProcJobChain: $allProcJobChain"
        )
        //任务优先级排序,任务的优先级由两个因素影响，
//        mainProcJobChain.sortWith(object : Comparator<JobInfo> {
//            override fun compare(o1: JobInfo?, o2: JobInfo?): Int {
//                return 0
//            }
//
//        })
        start = System.currentTimeMillis()
        Injector.injectCode(jlcAppDelegateClassInfo) { where, inputStream ->
            val makeClass = classPool.makeClass(inputStream)
            try {
                makeClass.defrost()
                mainProcJobChain.forEach {
                    makeClass.getDeclaredMethod("registerInMainProc")
                        .insertAfter(genInsertCode(it))
                }
                launcherProcJobChain.forEach {
                    makeClass.getDeclaredMethod("registerInLauncherProc")
                        .insertAfter(genInsertCode(it))
                }
                otherProcJobChain.forEach {
                    makeClass.getDeclaredMethod("registerInOtherProc")
                        .insertAfter(genInsertCode(it))
                }
                allProcJobChain.forEach {
                    makeClass.getDeclaredMethod("registerInMainProc")
                        .insertAfter(genInsertCode(it))
                    makeClass.getDeclaredMethod("registerInLauncherProc")
                        .insertAfter(genInsertCode(it))
                    makeClass.getDeclaredMethod("registerInOtherProc")
                        .insertAfter(genInsertCode(it))
                }
                makeClass.toBytecode()
            } catch (e: Exception) {
                P.error("could not insert code in ${makeClass.name};  ${jlcAppDelegateClassInfo?.mather2?.absolutePath}\n$e")
                byteArrayOf()
            } finally {
                makeClass.detach()
                P.info("inject code to JlcAppDelegate cost:${System.currentTimeMillis() - start}ms")
            }
        }
    }
}