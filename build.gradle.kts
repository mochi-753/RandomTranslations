plugins {
    id("java")
    id("net.neoforged.moddev.legacyforge").version("2.0.141")
    id("idea")
}

object Metadata {
    const val MOD_ID = "randomtranslations"
    const val MOD_NAME = "Random Translations"
    const val MOD_VERSION = "1.0.0"
    const val MOD_LICENSE = "MIT"
    const val MOD_DISPLAY_URL = ""
    const val MOD_AUTHORS = "Mochi753"
    const val MOD_DESCRIPTION = "The Crazy Mod That Ruins Every Translation"
    const val MOD_GROUP_ID = "io.github.mochi_753"

    const val MINECRAFT_VERSION = "1.20.1"
    const val MINECRAFT_VERSION_RANGE = "[1.20.1]"
    const val FORGE_VERSION = "47.4.10"
    const val FORGE_VERSION_RANGE = "[47.4,)"
    const val LOADER_VERSION_RANGE = "[47,)"
    const val PARCHMENT_VERSION = "2023.09.03"
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

group = Metadata.MOD_GROUP_ID
version = Metadata.MOD_VERSION

repositories {
    mavenCentral()
}

base {
    archivesName.set(Metadata.MOD_NAME.replace(" ", ""))
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

val localRuntime = configurations.create("localRuntime")
configurations {
    runtimeClasspath {
        extendsFrom(localRuntime)
    }
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to Metadata.MINECRAFT_VERSION,
        "minecraft_version_range" to Metadata.MINECRAFT_VERSION_RANGE,
        "forge_version" to Metadata.FORGE_VERSION,
        "forge_version_range" to Metadata.FORGE_VERSION_RANGE,
        "loader_version_range" to Metadata.LOADER_VERSION_RANGE,
        "mod_id" to Metadata.MOD_ID,
        "mod_name" to Metadata.MOD_NAME,
        "mod_version" to Metadata.MOD_VERSION,
        "mod_license" to Metadata.MOD_LICENSE,
        "mod_display_url" to Metadata.MOD_DISPLAY_URL,
        "mod_authors" to Metadata.MOD_AUTHORS,
        "mod_description" to Metadata.MOD_DESCRIPTION
    )
    inputs.properties(replaceProperties)

    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
        srcDir(generateModMetadata)
    }
}

legacyForge {
    version = Metadata.MINECRAFT_VERSION + "-" + Metadata.FORGE_VERSION

    parchment {
        mappingsVersion.set(Metadata.PARCHMENT_VERSION)
        minecraftVersion.set(Metadata.MINECRAFT_VERSION)
    }

    runs {
        configureEach {
            logLevel.set(org.slf4j.event.Level.DEBUG)
            systemProperty("forge.logging.markers", "REGISTRIES")
        }

        register("client") {
            client()
            gameDirectory.set(file("runs/client"))
        }

        register("server") {
            server()
            gameDirectory.set(file("runs/server"))
            programArguments.add("--nogui")
        }
    }

    mods {
        register(Metadata.MOD_ID) {
            sourceSet(sourceSets.main.get())
        }
    }

    ideSyncTasks.add(generateModMetadata)
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

mixin {
    add(sourceSets.main.get(), "mixins.randomtranslations.refmap.json")
    config("mixins.randomtranslations.json")
}

tasks.jar {
    manifest.attributes("MixinConfigs" to "mixins.randomtranslations.json")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.withType<JavaExec>().configureEach {
    standardInput = System.`in`
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
