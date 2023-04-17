package org.hapjs.bridge

import org.hapjs.bridge.annotation.*



interface Extension : MetaData {
    val name: String
    val methods: List<Method>
}

interface MetaData {
    var classname: String
    var superClasses: List<String>
}

private val d: (Int, MetaData, Int, MetaData) -> Int = l@{ i, item, j, other ->
    if (item.superClasses.contains(other.classname)) {
        return@l j
    } else if (other.superClasses.contains(item.classname)) {
        return@l i
    }
    -1
}

fun <T : MetaData> MutableList<T?>.removeParent(
    skip: ((T, T) -> Boolean)? = null,
) = remove(skip, deleteCondition = d)

/**
 *去掉继承链中的无效父类
 *
 * @param skip:跳过item与other的比较
 * @param deleteCondition:-1为没有需要删除的元素
 *
 */
fun <T> MutableList<T?>.remove(
    skip: ((T, T) -> Boolean)?,
    deleteCondition: (Int, T, Int, T) -> Int
) = apply {
    for (i in 0 until size) {
        val item = get(i) ?: continue
        for (j in i + 1 until size) {
            val other = get(j) ?: continue
            if (skip?.invoke(item, other) == true) continue
            val pos = deleteCondition(i, item, j, other)
            if (pos != -1) {
                set(pos, null)
                //如果pos为item的index，则break内部循环，从i+1的item找parent类
                if (pos == i) break
            }
        }
    }
    val iter = iterator()
    while (iter.hasNext()) {
        if (iter.next() == null) {
            iter.remove()
        }
    }
}

data class Method(
    val name: String,
    val isInstanceMethod: Boolean = false,
    val mode: org.hapjs.bridge.Extension.Mode,
    val type: org.hapjs.bridge.Extension.Type,
    val access: org.hapjs.bridge.Extension.Access,
    val normalize: org.hapjs.bridge.Extension.Normalize,
    val multiple: org.hapjs.bridge.Extension.Multiple,
    val alias: String = "",
    val permissions: Array<String>,
    val subAttrs: Array<String>,
    val residentType: ActionAnnotation.ResidentType,
)

fun getMethods(actions: Array<ActionAnnotation>?): List<Method> {
    val l = mutableListOf<Method>()
    if (actions.isNullOrEmpty()) return l
    for (action in actions) {
        l.add(
            Method(
                action.name,
                action.instanceMethod,
                action.mode,
                action.type,
                action.access,
                action.normalize,
                action.multiple,
                action.alias,
                action.permissions,
                action.subAttrs,
                action.residentType
            )
        )
    }
    return l
}

