package dev.hoodieboi.rainbowquartz

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class ItemConfigManager @Throws(IOException::class) constructor(private val filePath: String, private val plugin: Plugin) {

    private var file: File = File(plugin.dataFolder, filePath)
    private var configuration: YamlConfiguration

    init {
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        }
        configuration = YamlConfiguration.loadConfiguration(file)
        configuration.addDefault("items", listOf<Item>())
    }

    @Throws(IOException::class)
    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    fun getItems(): Set<Item> {
        val itemList: MutableList<*>? = configuration.getList("items")
        if (itemList == null) {
            plugin.logger.warning("Unable to load items from '$filePath', invalid or missing property 'items'")
            return setOf()
        }

        val items: MutableSet<Item> = mutableSetOf()
        items.addAll(itemList.filterIsInstance(Item::class.java))

        return items.toSet()
    }

    @Throws(IOException::class)
    fun saveItems(items: Collection<Item>) {
        configuration.set("items", items.toList())
        configuration.save(file)
    }
}