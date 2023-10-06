package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.KeyMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.enchanted
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent

@KeyMenu
class EditItemActionsMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) : EditItemMenu(viewer, builder) {
    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(ACTIONS_SLOT, inventory.getItem(ACTIONS_SLOT)?.enchanted())

        inventory.setItem(3, LinkItem.makeLink(
            "event",
            Material.BEDROCK,
            "Event"
        ))
    }
}