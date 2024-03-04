package com.olillin.rainbowquartz

import com.olillin.rainbowquartz.item.GuiItem
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

public class ItemConfigManager @Throws(IOException::class) constructor(
    private val filePath: String,
    private val plugin: Plugin
) {

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
        configuration.addDefault("items", listOf<GuiItem>())
    }

    @Throws(IOException::class)
    public fun reload() {
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    public fun getItems(): List<GuiItem> {
        val itemList: MutableList<*>? = configuration.getList("items")
        if (itemList == null) {
            plugin.logger.warning("Unable to load items from '$filePath', invalid or missing property 'items'")
            return listOf()
        }

        val items: List<GuiItem> = itemList.filterIsInstance<GuiItem>()

        return items
    }

    @Throws(IOException::class)
    public fun saveItems(items: Collection<GuiItem>) {
        configuration.set("items", items.toList())
        configuration.save(file)
    }
}