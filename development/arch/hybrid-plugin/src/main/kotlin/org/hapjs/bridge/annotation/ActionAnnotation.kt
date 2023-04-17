/*
 * Copyright (C) 2017, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge.annotation

import org.hapjs.bridge.Extension

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class ActionAnnotation(
    val name: String,
    val instanceMethod: Boolean = false,
    val mode: Extension.Mode,
    val type: Extension.Type = Extension.Type.FUNCTION,
    val access: Extension.Access = Extension.Access.NONE,
    val normalize: Extension.Normalize = Extension.Normalize.JSON,
    val multiple: Extension.Multiple = Extension.Multiple.SINGLE,
    val alias: String = "",
    val permissions: Array<String> = [],
    val subAttrs: Array<String> = [],
    val residentType: ResidentType = ResidentType.NONE
) {
    enum class ResidentType {
        NONE, USEABLE
    }
}