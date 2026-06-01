plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.google.mapsplatform.secrets).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.kotlin.parcelize).apply(false)
    alias(libs.plugins.google.gms.google.services).apply(false)
    alias(libs.plugins.google.firebase.crashlytics).apply(false)
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

/*
tasks.register<UpdateDexCrcInStringsXmlTask>("updateDexCrcInStringsXmlTask") {
    group = "verification"
    description = "Updates the `dex_crc` value in `strings.xml`."
}

open class UpdateDexCrcInStringsXmlTask : DefaultTask() {
    @TaskAction
    fun action() {
        val generateDexCrc = {
            val zipFile =
                ZipFile(file("${project.layout.buildDirectory}/outputs/apk/release/app-release.apk"))
            val zipEntries = zipFile.stream().iterator().asSequence().toList().filter {
                it.name.contains("dex")
            }

            val concatenatedCrcs = buildString {
                append(zipFile.getEntry("classes.dex").crc)
                for (i in 2..zipEntries.size) {
                    append(zipFile.getEntry("classes$i.dex").crc)
                }
            }

            Security.addProvider(BouncyCastleProvider())
            val digest = Blake2b.Blake2b256()
            val blake2Hash = digest.digest(concatenatedCrcs.toByteArray(Charsets.UTF_8))
            Base64.getEncoder().encodeToString(blake2Hash)
        }

        val dexCrcValue = generateDexCrc()

        val stringsFile = file("src/main/res/values/strings.xml")
        var stringsContent = stringsFile.readText()

        // Only update DEX CRC if it's different from the stored one.
        if (!stringsContent.contains(dexCrcValue)) {
            stringsContent = stringsContent.replace(
                "/<string name=\"dex_crc\">.*</string>/".toRegex(),
                "<string name=\"dex_crc\">$dexCrcValue</string>"
            )
            stringsFile.writeText(stringsContent)
        }
    }
}*/
