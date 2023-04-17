/*
 * Copyright (C) 2017, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class TypeAnnotation(val name: String, val isDefault: Boolean = false)