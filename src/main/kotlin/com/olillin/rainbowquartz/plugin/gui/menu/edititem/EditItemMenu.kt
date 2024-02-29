package com.olillin.rainbowquartz.plugin.gui.menu.edititem

import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.item.Item
import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.item.rainbowQuartzId
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.ImmutableMenu
import com.olillin.rainbowquartz.plugin.gui.menu.ItemEditorMenu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
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
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

public abstract class EditItemMenu(final override val viewer: HumanEntity, protected var builder: ItemBuilder) :
    ImmutableMenu() {
    private val title: Component = builder.build().getItem().itemMeta.displayName().let { itemName ->
        if (itemName != null) {
            Component.text("Editing item: ").append(itemName.compact().removeStyle())
        } else {
            Component.text("Editing item")
        }
    }
    final override var inventory: Inventory = Bukkit.createInventory(
        viewer,
        27,
        title,
    )

    @EventHandler(priority = EventPriority.HIGHEST)
    public fun onOpenEditItemMenu(event: InventoryOpenEvent) {
        // Create preview panel
        val previewPanel = ItemStack(Material.PURPLE_STAINED_GLASS_PANE)
        var meta = previewPanel.itemMeta
        meta.displayName(
            Component.text("Preview")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        )
        previewPanel.itemMeta = meta

        // Create preview item
        val previewItem = ItemStack(builder.build().getItem())
        meta = previewItem.itemMeta
        meta.rainbowQuartzId = null
        previewItem.itemMeta = meta

        // Items
        inventory.setItem(0, LinkItem.BACK)
        inventory.setItem(
            GENERAL_SLOT, LinkItem.makeLink(
                "general",
                Material.QUARTZ,
                Component.text("General").color(NamedTextColor.YELLOW),
                listOf(
                    Component.text("Change item name, etc")
                )
            )
        )
        inventory.setItem(
            RECIPES_SLOT, LinkItem.makeLink(
                "recipes",
                Material.CRAFTING_TABLE,
                Component.text("Recipes").color(NamedTextColor.YELLOW),
                listOf(
                    Component.text("Edit and create recipes")
                )
            )
        )
        inventory.setItem(
            ACTIONS_SLOT, LinkItem.makeLink(
                "actions",
                Material.FIREWORK_ROCKET,
                Component.text("Actions").color(NamedTextColor.LIGHT_PURPLE),
                listOf(
                    Component.text("Event stuff")
                )
            )
        )
        inventory.setItem(18, EMPTY_PANEL)
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(2, previewPanel)
        inventory.setItem(11, previewItem)
        inventory.setItem(20, previewPanel)
    }

    @EventHandler(priority = EventPriority.LOW)
    public fun onLinkEditItem(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "back" -> {
                applyChanges()
                backUntil(ItemEditorMenu(viewer, null)) { it is ItemEditorMenu }
            }

            "general" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                EditItemGeneralMenu(viewer, builder, previousMenu).open()
            }

            "recipes" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                EditItemRecipesMenu(viewer, builder, previousMenu).open()
            }

            "actions" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                EditItemActionsMenu(viewer, builder, previousMenu).open()
            }
        }
    }

    private fun applyChanges() {
        val item: Item = builder.build()
        if (RainbowQuartz.itemManager.containsItem(builder.id)) {
            // No changes have been made
            if (item == RainbowQuartz.itemManager.getItem(builder.id)) {
                viewer.sendMessage(Component.text("No changes have been made").color(NamedTextColor.RED))
                return
            }
            // Re-register item
            RainbowQuartz.itemManager.unregisterItem(builder.id)
            viewer.sendMessage(
                Component.empty().color(NamedTextColor.WHITE)
                    .append(Component.text("Successfully applied changes to ").color(NamedTextColor.GREEN))
                    .append(item.component())
            )
        } else {
            viewer.sendMessage(
                Component.empty().color(NamedTextColor.WHITE)
                    .append(Component.text("Successfully created ").color(NamedTextColor.GREEN))
                    .append(item.component())
            )
        }
        RainbowQuartz.itemManager.registerItem(item)
    }

    @EventHandler(priority = EventPriority.LOW)
    public fun onCloseEditItem(event: InventoryCloseEvent) {
        if (event.reason == InventoryCloseEvent.Reason.OPEN_NEW) return
        sendCancelledMessage(viewer)
    }

    protected companion object {
        public const val GENERAL_SLOT: Int = 1
        public const val RECIPES_SLOT: Int = 9
        public const val ACTIONS_SLOT: Int = 10

        public fun sendCancelledMessage(recipient: HumanEntity) {
            recipient.playSound(Sound.BLOCK_ANVIL_LAND)
            recipient.sendMessage(
                Component.text("Item editor was closed, no changes have been applied")
                    .color(NamedTextColor.RED)
            )
        }
    }
}

public fun Component.removeStyle(): Component {
    var component = this.style(Style.empty())
    if (component is TranslatableComponent) {
        val args = component.args().map { it.removeStyle() }
        component = component.args(args)
    }
    val children = component.children().map { it.removeStyle() }
    component = component.children(children)
    return component
}