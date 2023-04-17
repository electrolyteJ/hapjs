/*
 * Copyright (C) 2018, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class ModuleExtensionAnnotation(val name: String, val actions: Array<ActionAnnotation>)