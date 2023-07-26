package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.enchanted
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.plugin.Plugin

class EditItemRecipesMenu(viewer: HumanEntity, plugin: Plugin, builder: ItemBuilder) : EditItemMenu(viewer, plugin, builder) {
    init {
        inventory.setItem(RECIPES_SLOT, inventory.getItem(RECIPES_SLOT)?.enchanted())

        inventory.setItem(3, LinkItem.makeLink(
            "craft",
            Material.CRAFTING_TABLE,
            "Craft"
        ))
    }
}