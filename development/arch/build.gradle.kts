// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        maven { url = uri("./local-repo") }
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://maven.oschina.net/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()

    }
    dependencies {
        val AGP_VERSION :String by project
        classpath("com.android.tools.build:gradle:${AGP_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
allprojects {
    repositories {
        maven { url = uri("./local-repo") }
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://maven.oschina.net/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
    }
//    tasks.withType(JavaCompile::class.java).configureEach { task ->
//        task.options.encoding = 'UTF-8'
//        task.sourceCompatibility = JavaVersion.VERSION_1_8
//        task.targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class.java).configureEach { task ->
//        task.kotlinOptions {
//            jvmTarget = '1.8'
//        }
//    }
    configurations.all {
        resolutionStrategy {
            val JAVASSIST_VERSION :String by project
            force("org.javassist:javassist:${JAVASSIST_VERSION}")
        }
    }

}

tasks.register("clean", Delete::class.java) {
    description = "Remove all the build files and intermediate build outputs"
//    delete(allprojects.map { it.buildDir })
    delete(rootProject.buildDir)
}