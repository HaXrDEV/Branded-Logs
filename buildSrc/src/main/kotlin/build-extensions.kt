import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.gradle.language.jvm.tasks.ProcessResources

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = requireNotNull(project.prop("mod.id")) { "Missing 'mod.id'" }
    val name: String get() = requireNotNull(project.prop("mod.name")) { "Missing 'mod.name'" }
    val version: String get() = requireNotNull(project.prop("mod.version")) { "Missing 'mod.version'" }
    val group: String get() = requireNotNull(project.prop("mod.group")) { "Missing 'mod.group'" }
    val description: String get() = requireNotNull(project.prop("mod.description")) { "Missing 'mod.description'" }

    fun prop(key: String) = requireNotNull(project.prop("mod.$key")) { "Missing 'mod.$key'" }
    fun dep(key: String) = requireNotNull(project.prop("deps.$key")) { "Missing 'deps.$key'" }
}



/**
 * This function is responsible for writing the publishing GitHub-Action workflow when building, and runs for each active version.
 */
fun Project.appendGithubActionPublish(minecraftVersion: String, mcTitle: String) {

    var actionFile = file("$rootDir/.github/workflows/publish.yml")
    var releaseText = StringBuilder()

    var mcpublishVersion = "3.3.0"

    val curseforgeid = property("publish.curseforge").toString()
    val modrinthid = property("publish.modrinth").toString()

    val modloader = prop("loom.platform")?.uppercaseFirstChar()
    val version = "$minecraftVersion-$modloader"

    // Append stuff for CurseForge publishing
    releaseText.append("""
      - name: Publish-$version-Curseforge
        uses: Kir-Antipov/mc-publish@v$mcpublishVersion
        with:
          curseforge-id: $curseforgeid
          curseforge-token: ${'$'}{{secrets.CURSEFORGE_TOKEN}}
          name: v${'$'}{{github.ref_name}} for $modloader $mcTitle
          files: 'build/libs/${'$'}{{github.ref_name}}/${modloader?.lowercase()}/!(*-@(dev|sources|javadoc|all)).jar;build/libs/${'$'}{{github.ref_name}}/${modloader?.lowercase()}/*[+.-]$minecraftVersion.jar'

      - name: Publish-$version-Modrinth
        uses: Kir-Antipov/mc-publish@v$mcpublishVersion
        with:
          modrinth-id: $modrinthid
          modrinth-token: ${'$'}{{secrets.MODRINTH_TOKEN}}
          name: v${'$'}{{github.ref_name}} for $modloader $mcTitle
          files: 'build/libs/${'$'}{{github.ref_name}}/${modloader?.lowercase()}/!(*-@(dev|sources|javadoc|all)).jar;build/libs/${'$'}{{github.ref_name}}/${modloader?.lowercase()}/*[+.-]$minecraftVersion.jar'
    """)

    actionFile.appendText(releaseText.toString())
}