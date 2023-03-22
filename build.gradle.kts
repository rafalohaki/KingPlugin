plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

group = "com.rafalohaki"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
}

tasks {
    val pluginJar by creating(Jar::class) {
        manifest {
            attributes["Main-Class"] = "com.rafalohaki.kingplugin.KingPlugin"
        }
        from(sourceSets.main.get().output)
        archiveFileName.set("KingPlugin.jar")
    }
    build {
        dependsOn(pluginJar)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
