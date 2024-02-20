package com.olillin.rainbowquartz.plugin.gui.menu.edititem

import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.enchanted
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import com.olillin.rainbowquartz.plugin.gui.menu.popup.ComponentPopup
import com.olillin.rainbowquartz.plugin.gui.menu.popup.LorePopup
import com.olillin.rainbowquartz.plugin.gui.menu.popup.MaterialPopup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent

class EditItemGeneralMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) :
    EditItemMenu(viewer, builder) {

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        val itemName = (builder.build().item.displayName() as? TranslatableComponent)?.args()?.get(0)
            ?: Component.text("Name Unavailable").color(NamedTextColor.DARK_GRAY)

        // Items
        inventory.setItem(GENERAL_SLOT, inventory.getItem(GENERAL_SLOT)?.enchanted())

        inventory.setItem(3, LinkItem.makeLink(
                "rename",
                Material.NAME_TAG,
                Component.text("Rename").color(NamedTextColor.LIGHT_PURPLE),
                listOf(
                    Component.text("Current name"),
                    Component.text(" ").color(NamedTextColor.WHITE).append(itemName)
                )
            )
        )
        inventory.setItem(4, LinkItem.makeLink(
                "change_material",
                builder.getMaterial(),
                Component.text("Change material").color(NamedTextColor.LIGHT_PURPLE),
                listOf(
                        Component.text("Current material"),
                        Component.text(" ").color(NamedTextColor.WHITE).append(Component.translatable(builder.getMaterial()))
                )
        ))
        inventory.setItem(5, LinkItem.makeLink(
                "lore",
                Material.WRITABLE_BOOK,
                Component.text("Edit lore").color(NamedTextColor.LIGHT_PURPLE),
                builder.getLore()
        ))
        inventory.setItem(6, EMPTY_PANEL)
        inventory.setItem(7, EMPTY_PANEL)
        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(12, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(14, EMPTY_PANEL)
        inventory.setItem(15, EMPTY_PANEL)
        inventory.setItem(16, EMPTY_PANEL)
        inventory.setItem(17, EMPTY_PANEL)
        inventory.setItem(21, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
        inventory.setItem(23, EMPTY_PANEL)
        inventory.setItem(24, EMPTY_PANEL)
        inventory.setItem(25, EMPTY_PANEL)
        inventory.setItem(26, EMPTY_PANEL)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "rename" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ComponentPopup(viewer, placeholder = builder.getName(), previousMenu = this) { name ->
                    builder.setName(name)
                }.open()
            }
            "change_material" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                MaterialPopup(viewer, placeholder = builder.getMaterial(), previousMenu = this) { material ->
                    builder.setMaterial(material)
                }.open()
            }
            "lore" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                LorePopup(viewer, placeholder = builder.getLore(), previousMenu = this) { lore ->
                    builder.setLore(lore)
                }.open()
            }
        }
    }
}