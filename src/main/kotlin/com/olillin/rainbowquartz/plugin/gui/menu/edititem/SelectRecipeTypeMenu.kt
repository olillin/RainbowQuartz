package com.olillin.rainbowquartz.plugin.gui.menu.edititem

import com.olillin.rainbowquartz.craft.ShapedRecipe
import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.ImmutableMenu
import com.olillin.rainbowquartz.plugin.gui.menu.edititem.recipe.ShapedRecipePopup
import com.olillin.rainbowquartz.plugin.gui.menu.fill
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

class SelectRecipeTypeMenu(override val viewer: HumanEntity, private val builder: ItemBuilder, override val previousMenu: EditItemMenu) : ImmutableMenu() {
    override var inventory: Inventory = Bukkit.createInventory(viewer, 18, Component.text("Select recipe type"))

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        inventory.addItem(
            LinkItem.makeLink(
                "shaped",
                ShapedRecipe.material,
                Component.text("Shaped crafting recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.setItem(13, LinkItem.BACK)
        inventory.fill(EMPTY_PANEL)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "shaped" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ShapedRecipePopup(viewer, null, builder.build().getItem(), this) {
                    if (it != null) {
                        builder.addRecipe(it)
                    }
                }.open()
            }
            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                previousMenu.open()
            }
        }
    }
}
