package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.craft.ShapedRecipe
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.recipe.ShapedRecipeMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.fill
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.inventory.Inventory

class SelectRecipeTypeMenu(override val viewer: HumanEntity, private val builder: ItemBuilder, override val previousMenu: EditItemMenu) : ImmutableMenu() {
    override var inventory: Inventory = Bukkit.createInventory(viewer, 18, Component.text("Select recipe type"))

    init {
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
                ShapedRecipeMenu(viewer, builder, this).open()
            }
            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                previousMenu.open()
            }
        }
    }
}
