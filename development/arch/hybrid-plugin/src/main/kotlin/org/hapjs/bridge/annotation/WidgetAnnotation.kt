/*
 * Copyright (C) 2017, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class WidgetAnnotation(
    val name: String,
    val types: Array<TypeAnnotation> = [],
    val methods: Array<String> = [],
    val needDeleteSuperClasses: Boolean = false
)