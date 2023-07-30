package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ItemEditorMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

abstract class EditItemMenu(final override val viewer: HumanEntity, protected val plugin: Plugin, protected val builder: ItemBuilder) : ImmutableMenu() {
    final override var inventory: Inventory

    companion object {
        const val GENERAL_SLOT = 1
        const val RECIPES_SLOT = 9
        const val ACTIONS_SLOT = 10

        fun sendCancelledMessage(recipient: HumanEntity) {
            recipient.playSound(Sound.BLOCK_ANVIL_LAND)
            recipient.sendMessage(Component.text("Item editor was closed, no changes have been applied").color(NamedTextColor.RED))
        }
    }

    init {
        // Initialize inventory
        val itemName = (builder.build().item.displayName() as? TranslatableComponent)?.args()?.get(0) ?: Component.text("Name Unavailable").color(
            NamedTextColor.DARK_GRAY)
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
        inventory.setItem(0, LinkItem.BACK)
        inventory.setItem(GENERAL_SLOT, LinkItem.makeLink(
            "general",
            Material.QUARTZ,
            Component.text("General").color(NamedTextColor.YELLOW),
            listOf(
                Component.text("Change item name, etc")
            )
        ))
        inventory.setItem(RECIPES_SLOT, LinkItem.makeLink(
            "recipes",
            Material.CRAFTING_TABLE,
            Component.text("Recipes").color(NamedTextColor.YELLOW),
            listOf(
                Component.text("Edit and create recipes")
            )
        ))
        inventory.setItem(ACTIONS_SLOT, LinkItem.makeLink(
            "actions",
            Material.FIREWORK_ROCKET,
            Component.text("Actions").color(NamedTextColor.LIGHT_PURPLE),
            listOf(
                Component.text("Event stuff")
            )
        ))
        inventory.setItem(18, EMPTY_PANEL)
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(2, previewPanel)
        inventory.setItem(11, previewItem)
        inventory.setItem(20, previewPanel)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onLinkEditItem(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                applyChanges()
                ItemEditorMenu(viewer, plugin).show()
            }
            "general" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                EditItemGeneralMenu(viewer, plugin, builder).show()
            }
            "recipes" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                EditItemRecipesMenu(viewer, plugin, builder).show()
            }
            "actions" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                EditItemActionsMenu(viewer, plugin, builder).show()
            }
        }
    }

    private fun applyChanges() {
        if (RainbowQuartz.itemManager.containsItem(builder.key)) {
            RainbowQuartz.itemManager.unregisterItem(builder.key)
            viewer.sendMessage(Component.empty().color(NamedTextColor.WHITE)
                .append(Component.text("Successfully applied changes to ").color(NamedTextColor.GREEN))
                .append(builder.getName() ?: Component.text("item").color(NamedTextColor.GREEN)))
        } else {
            viewer.sendMessage(Component.empty().color(NamedTextColor.WHITE)
                .append(Component.text("Successfully created ").color(NamedTextColor.GREEN))
                .append(builder.getName() ?: Component.text("item").color(NamedTextColor.GREEN)))
        }
        RainbowQuartz.itemManager.registerItem(builder.build())
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onCloseEditItem(event: InventoryCloseEvent) {
        if (event.reason == InventoryCloseEvent.Reason.OPEN_NEW) return
        sendCancelledMessage(viewer)
    }
}

fun Component.removeStyle(): Component {
    var component = this.style(Style.empty())
    if (component is TranslatableComponent) {
        val args = component.args().map { it.removeStyle() }
        component = component.args(args)
    }
    val children = component.children().map { it.removeStyle() }
    component = component.children(children)
    return component
}