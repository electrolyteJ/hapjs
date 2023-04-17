import org.gradle.api.internal.classpath.ModuleRegistry
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
//import org.gradle.configurationcache.extensions.serviceOf
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("java-gradle-plugin")
}
val AGP_VERSION :String by project
val JAVASSIST_VERSION :String by project
val KOTLIN_VERSION :String by project
val groupId :String by project
gradlePlugin {
    plugins {
        create("hybridplugin") {
            id = "${groupId}.hybrid-plugin"
            implementationClass = "HybridPlugin"
            displayName = "hybrid-plugin"
            description = "hybrid-plugin"
        }
    }
}

version = "1.0.0"
dependencies {
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:${AGP_VERSION}")
    implementation("org.javassist:javassist:${JAVASSIST_VERSION}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${KOTLIN_VERSION}")
    testImplementation("junit:junit:4.13.2")

    testRuntimeOnly(
        files(
            serviceOf<ModuleRegistry>()
                .getModule("gradle-tooling-api-builders")
                .classpath
                .asFiles
                .first()))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
apply(plugin = "org.jetbrains.kotlin.jvm")
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions { jvmTarget = JavaVersion.VERSION_11.majorVersion }
//}

tasks.withType<Test>().configureEach {
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
