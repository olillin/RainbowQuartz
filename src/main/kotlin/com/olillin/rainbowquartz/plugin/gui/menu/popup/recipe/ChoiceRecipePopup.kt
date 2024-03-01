package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.*
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

public class ChoiceRecipePopup(
    override val viewer: HumanEntity, private val previewItem: ItemStack, override val previousMenu: Menu,
    override val callback: (Recipe<*, *>) -> Unit
) :
    ImmutableMenu(), Popup<Recipe<*, *>> {

    override var inventory: Inventory = Bukkit.createInventory(viewer, 18, Component.text("Select recipe type"))

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    public fun onOpen(event: InventoryOpenEvent) {
        inventory.addItem(
            LinkItem.makeLink(
                "shaped",
                ShapedRecipe.ICON,
                Component.text("Shaped crafting recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "shapeless",
                ShapelessRecipe.ICON,
                Component.text("Shapeless crafting recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "furnace",
                FurnaceRecipe.ICON,
                Component.text("Furnace recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "smoking",
                SmokingRecipe.ICON,
                Component.text("Smoking recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "blasting",
                BlastingRecipe.ICON,
                Component.text("Blasting recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "campfire",
                CampfireRecipe.ICON,
                Component.text("Campfire recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "smithing",
                SmithingTransformRecipe.ICON,
                Component.text("Smithing recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.addItem(
            LinkItem.makeLink(
                "stonecutting",
                StonecuttingRecipe.ICON,
                Component.text("Stonecutting recipe").color(NamedTextColor.AQUA)
            )
        )
        inventory.setItem(13, LinkItem.CANCEL)
        inventory.fill(EMPTY_PANEL)
    }

    @EventHandler
    public fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "shaped" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ShapedRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "shapeless" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ShapelessRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "furnace" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                FurnaceRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "smoking" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                SmokingRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "blasting" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                BlastingRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "campfire" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                CampfireRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "smithing" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                SmithingTransformRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "stonecutting" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                StonecuttingRecipePopup(viewer, null, previewItem, this, internalCallback).open()
            }

            "cancel" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                back()
            }
        }
    }

    private val internalCallback: (Recipe<*, *>?) -> Unit = {
        if (it != null) {
            callback(it)
        }
        back()
    }
}
