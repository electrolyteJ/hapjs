/*
 * Copyright (C) 2017, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class FeatureExtensionAnnotation(
    val name: String,
    val actions: Array<ActionAnnotation>,
    val residentType: ResidentType = ResidentType.FORBIDDEN
) {
    enum class ResidentType {
        FORBIDDEN, USEABLE, RESIDENT_NORMAL, RESIDENT_IMPORTANT
    }
}