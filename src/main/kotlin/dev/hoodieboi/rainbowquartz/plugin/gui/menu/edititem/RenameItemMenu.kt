package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCloseEvent.Reason
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class RenameItemMenu(override val viewer: HumanEntity, val plugin: Plugin, private val builder: ItemBuilder) :
    ImmutableMenu() {
    override lateinit var inventory: AnvilInventory

    override fun show() {
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        viewer.openAnvil(null, true)
        inventory = viewer.openInventory.topInventory as AnvilInventory
        // Set item in first slot
        inventory.firstItem = if (builder.hasName()) {
            ItemBuilder(builder)
                .setName(serializer.serialize(unformatName(builder.getName()!!)))
                .build().item
        } else builder.build().item
        inventory.firstItem = builder.build().item
    }

    private fun unformatName(name: Component): Component {
        return if (name.color() == NamedTextColor.WHITE) {
            name.color(null)
        } else name
    }

    @EventHandler
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val name = parseName(event.inventory) ?: return
        event.result = builder.setName(name).build().item
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.slotType == InventoryType.SlotType.RESULT && event.currentItem?.itemMeta?.rainbowQuartzId != null) {
            val inventory = event.inventory as? AnvilInventory
            if (inventory == null) {
                event.whoClicked.sendMessage(
                    Component.text("An unexpected error occurred when renaming item").color(NamedTextColor.RED)
                )
                return
            }
            val name: Component? = parseName(inventory)
            if (name == null) {
                event.whoClicked.sendMessage(
                    Component.text("An unexpected error occurred when renaming item").color(NamedTextColor.RED)
                )
                return
            }
            viewer.playSound(Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT)
            EditItemGeneralMenu(event.whoClicked, plugin, builder.setName(name)).show()
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        event.view.topInventory.clear() // Stop items from being refunded into player inventory
        if (event.reason == Reason.OPEN_NEW) return
        EditItemMenu.sendCancelledMessage(event.player)
    }

    private fun parseName(inventory: AnvilInventory): Component? {
        val text = inventory.renameText
        if (text == null) {
            val itemStack = ItemStack(Material.BARRIER)
            val meta = itemStack.itemMeta
            meta.displayName(Component.text("Must be different than current name").color(NamedTextColor.RED))
            itemStack.itemMeta = meta
            inventory.result = itemStack
            return null
        }
        return Item.formatName(serializer.deserialize(text))
    }

    companion object {
        val serializer = LegacyComponentSerializer.legacy('&')
    }
}