package org.hapjs.bridge

import javassist.CtClass
import org.hapjs.bridge.annotation.EventTargetAnnotation

class EventTargetProcessor : BaseProcessor<EventTarget>() {
    override val modifiedClassName = "org.hapjs.event.EventTargetDataSetImpl"
    override fun supportAnnotation() = setOf(EventTargetAnnotation::class.java)
    override fun map(ctClass: CtClass): EventTarget {
        val eventTargetAnnotation =
            ctClass.getAnnotation(EventTargetAnnotation::class.java) as EventTargetAnnotation
        return EventTarget(
            eventTargetAnnotation.eventNames,
            ctClass.name,
            ctClass.getSuperclasses()
        )
    }

    override fun process(modifiedCtClass: CtClass, sons: List<EventTarget>) {
        //todo:是否抛出异常让开发者修改会更佳合理而不是remove
        val eventTargets = sons.toMutableList<EventTarget?>().removeParent()
        if (eventTargets.isEmpty()) return
        //重组装列表event target 为 event为key的map数据类型
        val allEventTargetMap = mutableMapOf<String, MutableList<EventTarget>>()
        for (eventTarget in eventTargets) {
            if (eventTarget == null) continue
            for (event in eventTarget.eventNames) {
                var targets = allEventTargetMap[event]
                if (targets == null) {
                    targets = mutableListOf()
                    allEventTargetMap[event] = targets
                }
                targets.add(eventTarget)
            }
        }
        allEventTargetMap.forEach { (event, evenTargets) ->
            evenTargets.forEach { et ->
                modifiedCtClass.getDeclaredMethod("initEventTargetMetaData")
                    .insertAfter(buildString {
                        append(
                            "org.hapjs.event.EventTargetMetaData eventTarget_$event = new org.hapjs.event.EventTargetMetaData(" +
                                    "new String[]{${et.eventNames.joinToString(",") { "\"$it\"" }}}," +
                                    "\"${et.classname}\"" +
                                    ");"
                        )
                        append("org.hapjs.event.EventTargetDataSetImpl.put(\"${event}\", eventTarget_$event);")
                    })
            }
        }
    }
}

data class EventTarget(
    val eventNames: Array<String>,
    override var classname: String,
    override var superClasses: List<String>
): MetaData

//去掉继承链的无效类
fun <T : EventTarget?> MutableList<T?>.removeParent() = removeParent(null)
