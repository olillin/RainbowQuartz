package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.item.Item
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCloseEvent.Reason
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin

class EditItemMenu(val viewer: HumanEntity, val plugin: Plugin, private val builder: Item.ItemBuilder) : Menu() {
    override val inventory: Inventory
    init {
        // Initialize inventory
        val itemName = (builder.build().item.displayName() as? TranslatableComponent)?.args()?.get(0) ?: Component.text("Name Unavailable").color(NamedTextColor.DARK_GRAY)
        inventory = Bukkit.createInventory(viewer, 27, Component.text("Editing item: ").append(itemName.compact().removeStyle()))

        // Create preview panel
        val previewPanel = ItemStack(Material.PURPLE_STAINED_GLASS_PANE)
        var meta = previewPanel.itemMeta
        meta.displayName(Component.text("Preview").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
        previewPanel.itemMeta = meta

        // Create preview item
        val previewItem = ItemStack(builder.build().item)
        meta = previewItem.itemMeta
        meta.rainbowQuartzId = null
        previewItem.itemMeta = meta

        // Items
        inventory.setItem(0, Item.ItemBuilder(NamespacedKey(plugin, "back"), Material.ARROW)
            .setName(Component.text("Back").color(NamedTextColor.RED))
            .build().item)
        inventory.setItem(1, Item.ItemBuilder(NamespacedKey(plugin, "actions"), Material.FIREWORK_ROCKET)
            .setName(Component.text("Actions").color(NamedTextColor.LIGHT_PURPLE))
            .addLore("Event stuff")
            .build().item)
        inventory.setItem(2, EMPTY_PANEL)
        inventory.setItem(3, Item.ItemBuilder(NamespacedKey(plugin, "rename"), Material.NAME_TAG)
            .setName(Component.text("Rename").color(NamedTextColor.LIGHT_PURPLE))
            .addLore("Current name")
            .addLore(Component.text(" ").color(NamedTextColor.WHITE).append(itemName))
            .build().item)
        inventory.setItem(5, EMPTY_PANEL)
        inventory.setItem(6, EMPTY_PANEL)
        inventory.setItem(7, EMPTY_PANEL)
        inventory.setItem(8, previewPanel)
        inventory.setItem(9, Item.ItemBuilder(NamespacedKey(plugin, "general"), Material.QUARTZ)
            .setName(Component.text("General").color(NamedTextColor.YELLOW))
            .addLore("Change item name, etc")
            .build().item)
        inventory.setItem(10, EMPTY_PANEL)
        inventory.setItem(11, EMPTY_PANEL)
        inventory.setItem(12, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(14, EMPTY_PANEL)
        inventory.setItem(15, EMPTY_PANEL)
        inventory.setItem(16, EMPTY_PANEL)
        inventory.setItem(17, previewItem)
        inventory.setItem(18, Item.ItemBuilder(NamespacedKey(plugin, "recipes"), Material.CRAFTING_TABLE)
            .setName(Component.text("Recipes").color(NamedTextColor.YELLOW))
            .addLore("Edit and create recipes")
            .build().item)
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(20, EMPTY_PANEL)
        inventory.setItem(21, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
        inventory.setItem(23, EMPTY_PANEL)
        inventory.setItem(24, EMPTY_PANEL)
        inventory.setItem(25, EMPTY_PANEL)
        inventory.setItem(26, previewPanel)

        viewer.openInventory(inventory)
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (event.currentItem == null) return
        // Ignore player inventory
        if (event.clickedInventory is PlayerInventory) {
            // Allow player inventory manipulation
            if (!event.isShiftClick) {
                event.isCancelled = false
            }
            return
        }

        event.isCancelled = true

        val id = event.currentItem?.itemMeta?.rainbowQuartzId ?: return
        when (id.key) {
            "back" -> {
                ItemEditorMenu(event.whoClicked, plugin)
            }
            "rename" -> {
                RenameItemMenu(viewer, plugin, builder)
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        when (event.reason) {
            Reason.OPEN_NEW -> {}
            else -> {
                sendCancelMessage(event.player)
            }
        }
    }

    companion object {
        fun sendCancelMessage(recipient: HumanEntity) {
            recipient.sendMessage(Component.text("Item editor was closed, no changes have been applied").color(NamedTextColor.RED))
        }
    }
}

fun Component.removeStyle(): Component {
    var component = this.style(Style.empty())
    if (component is TranslatableComponent) {
        val args = component.args().map{c -> c.removeStyle()}
        component = component.args(args)
    }
    val children = component.children().map{c -> c.removeStyle()}
    component = component.children(children)
    return component
}