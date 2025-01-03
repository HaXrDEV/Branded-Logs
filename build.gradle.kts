plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

val minecraft = stonecutter.current.version

version = "${mod.version}+$minecraft"
base {
    archivesName.set("${mod.id}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.prop("loom.platform")
})

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    //mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
    loom.silentMojangMappingsLicense()
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
    //modImplementation("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_api")}")
    "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }

    modApi("me.shedaniel.cloth:cloth-config-fabric:${mod.dep("cloth_config_version")}") {
        exclude("net.fabricmc.fabric-api")
    }

}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}