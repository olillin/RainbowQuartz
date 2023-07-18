package dev.hoodieboi.rainbowquartz.plugin.gui

import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ItemEditorMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.MainMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.plugin.Plugin

class MenuManager(val plugin: Plugin) {
    val MAIN_MENU = MainMenu(plugin, 1)
    val ITEM_EDITOR = ItemEditorMenu(plugin, 2)

    fun menus(): Set<Menu> = setOf(
            MAIN_MENU,
            ITEM_EDITOR
        )
}