@file:Suppress("PropertyName", "VariableNaming")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.iridium)
    alias(libs.plugins.iridium.publish)
    alias(libs.plugins.iridium.upload)
}

repositories {
    maven("https://teamvoided.org/releases") { content { includeGroup("org.teamvoided") } }
    maven("https://teamvoided.org/snapshots") { content { includeGroup("org.teamvoided") } }
    maven("https://maven.fzzyhmstrs.me/") { name = "FzzyMaven"; content { includeGroup("me.fzzyhmstrs") } }
    maven("https://maven.terraformersmc.com/") {
        name = "Terraformers"
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }
    maven("https://api.modrinth.com/maven") { content { includeGroup("maven.modrinth") } }
    mavenCentral()
}

modSettings {
    entrypoint("main", "org.teamvoided.creative_works.CreativeWorks::commonInit")
    entrypoint("client", "org.teamvoided.creative_works.CreativeWorks::clientInit")
    entrypoint("fabric-datagen", "org.teamvoided.creative_works.data.gen.CreativeWorksData")
    mixinFile("${modId()}.mixins.json")
    accessWidener("${modId()}.accesswidener")
}


dependencies {
    modImplementation(fileTree("libs"))
    // Dependencies
    modImplementation(libs.imguimc)
    include(libs.imguimc)
    modImplementation(libs.fzzy.config)
    // QoL
    modImplementation(libs.modmenu)
    modCompileOnly("${libs.emi.get()}:api")
    modLocalRuntime(libs.emi)
}

val username = "Endoside"
val uuid: String? = "a5fc6689-7d19-4c39-a04e-95e4ec460298"

loom {
    runs {
        named("client") {
            programArgs("--username", username)
            uuid?.let { programArgs("--uuid", uuid) }
        }

        create("TestWorld") {
            client()
            ideConfigGenerated(true)
            runDir("run")
            programArgs("--quickPlaySingleplayer", "test", "--username", username)
            uuid?.let { programArgs("--uuid", uuid) }
        }

        create("DataGen") {
            client()
            ideConfigGenerated(true)
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=${modSettings.modId()}")
            runDir("build/datagen")
        }
    }
}

sourceSets["main"].resources.srcDir("src/main/generated")

tasks {
    val targetJavaVersion = 21
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
    }

    withType<KotlinCompile>().all {
        compilerOptions.jvmTarget = JvmTarget.JVM_21
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.toVersion(targetJavaVersion).toString()))
        withSourcesJar()
    }
}

publishScript {
    releaseRepository("TeamVoided", "https://maven.teamvoided.org/snapshots")
    publication(modSettings.modId(), false)
    publishSources(true)
}
