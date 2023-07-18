package dev.hoodieboi.rainbowquartz
//
//import org.bukkit.configuration.file.YamlConfiguration
//import java.io.IOException
//import java.nio.file.Path
//import kotlin.io.path.bufferedReader
//
//class ConfigManager(val filePath: Path) {
//
//    var configuration: ItemConfiguration = ItemConfiguration()
//
//    @Throws(IOException::class)
//    fun read() {
//        val reader = filePath.bufferedReader()
//        configuration = ItemConfiguration.loadConfiguration(reader)
//    }
//
//    fun write(configuration: ItemConfiguration) {
//
//    }
//}