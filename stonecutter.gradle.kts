plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}
stonecutter active "1.21.3" /* [SC] DO NOT EDIT */
stonecutter.automaticPlatformConstants = true

// Builds every version into `build/libs/{mod.version}/{loader}`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

// Builds loader-specific versions into `build/libs/{mod.version}/{loader}`
for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()
    stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
        group = "project"
        versions { branch, _ -> branch == it.id }
        ofTask("buildAndCollect")
    }
}

// Runs active versions for each loader
for (it in stonecutter.tree.nodes) {
    if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) it.tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("run$type")
    }
}



// GitHub Action Stuff
var releaseText = StringBuilder()

releaseText.append("""name: Create Release
on:
  release:
    types: [published]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: checkout repository
        uses: actions/checkout@v2

      - name: setup jdk 21
        uses: actions/setup-java@v1
        with:
          java-version: 21

      - name: make gradle wrapper executable
        run: chmod +x ./gradlew

      - name: build
        run: ./gradlew chiseledBuild
""")


var actionFile = file("$rootDir/.github/workflows/publish.yml")
if (actionFile.exists()) {
    actionFile.writeText(releaseText.toString())
}
else {
    actionFile.createNewFile()
    actionFile.writeText(releaseText.toString())
}