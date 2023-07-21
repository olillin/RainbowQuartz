package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.enchanted
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.plugin.Plugin

class EditItemActionsMenu(viewer: HumanEntity, plugin: Plugin, builder: ItemBuilder) : EditItemMenu(viewer, plugin, builder) {
    init {
        inventory.setItem(ACTIONS_SLOT, inventory.getItem(ACTIONS_SLOT)?.enchanted())

        inventory.setItem(3, LinkItem.makeLink(
            "event",
            Material.BEDROCK,
            "Event"
        ))
    }
}