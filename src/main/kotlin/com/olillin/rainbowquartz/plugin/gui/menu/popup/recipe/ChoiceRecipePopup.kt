package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.craft.ShapedRecipe
import com.olillin.rainbowquartz.craft.ShapelessRecipe
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.ImmutableMenu
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.fill
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import com.olillin.rainbowquartz.plugin.gui.menu.popup.Popup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ChoiceRecipePopup(override val viewer: HumanEntity, private val result: ItemStack, override val previousMenu: Menu,
                        override val callback: (Recipe) -> Unit
) :
    ImmutableMenu(), Popup<Recipe> {
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
        inventory.addItem(
            LinkItem.makeLink(
                "shapeless",
                ShapelessRecipe.material,
                Component.text("Shapeless crafting recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.setItem(13, LinkItem.CANCEL)
        inventory.fill(EMPTY_PANEL)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "shaped" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ShapedRecipePopup(viewer, null, result, this, internalCallback).open()
            }
            "shapeless" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ShapelessRecipePopup(viewer, null, result, this, internalCallback).open()
            }
            "cancel" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                back()
            }
        }
    }

    private val internalCallback: (Recipe?) -> Unit = {
        if (it != null) {
            callback(it)
        }
        back()
    }
}
