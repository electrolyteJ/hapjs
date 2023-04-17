/*
 * Copyright (C) 2019, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class EventTargetAnnotation(val eventNames: Array<String>)