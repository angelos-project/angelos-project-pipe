plugins {
    kotlin("multiplatform") version "1.9.24"
    `maven-publish`
}

group = "org.angproj.io"
version = "0.1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(IR) {
        browser {}
        nodejs{
            testTask{
                useKarma()
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting{
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
                implementation("org.angproj.aux.util:angelos-project-aux:0.9.8")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("net.java.dev.jna:jna:5.14.0")
            }
        }
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

publishing {
    repositories {
        maven {}
    }
    publications {
        getByName<MavenPublication>("kotlinMultiplatform") {
            artifactId = rootProject.name
        }
    }
}