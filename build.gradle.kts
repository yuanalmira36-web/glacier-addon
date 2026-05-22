plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
}

version = "1.0.0"
group = "com.example.addon"

dependencies {
    minecraft("com.mojang:minecraft:1.21.1")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.15.11")
    implementation("meteordevelopment:meteor-client:snapshots")
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}
