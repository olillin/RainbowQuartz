package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.recipe

import dev.hoodieboi.rainbowquartz.craft.Recipe
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.ImmutableMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.ConfirmationMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.SelectRecipeTypeMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class EditRecipeMenu : ImmutableMenu() {
    protected abstract val builder: ItemBuilder
    abstract override val previousMenu: SelectRecipeTypeMenu
    protected abstract val title: String
    override var inventory: Inventory = Bukkit.createInventory(viewer, 27, Component.text(title))

    protected var resultAmount = 1
    private val grid: List<ItemStack?>
        get() = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
            .map { inventory.getItem(it) }

    companion object {
        const val PREVIEW_SLOT = 15
    }

    init {
        inventory.setItem(0, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(18, LinkItem.BACK)

        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(
            17, LinkItem.makeLink(
                "submit",
                Material.CRAFTING_TABLE,
                Component.text("Create recipe").color(NamedTextColor.GREEN)
            )
        )
        inventory.setItem(
            26, LinkItem.makeLink(
                "delete",
                Material.CAULDRON,
                Component.text("Delete recipe").color(NamedTextColor.RED)
            )
        )

        updatePreview()
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "submit" -> {
                val recipe = createRecipe()
                builder.addRecipe(recipe)
                back()
            }

            "delete" -> {
                viewer.playSound(Sound.BLOCK_ANVIL_PLACE)
                ConfirmationMenu(viewer, { result ->
                    if (result) {
                        builder.removeRecipe(createRecipe())
                    }
                }, "Are you sure you want to delete this recipe?", previousMenu)
            }

            "add_amount_1" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                resultAmount++
                updatePreview()
            }

            "add_amount_16" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                if (resultAmount == 1) {
                    resultAmount = 16
                } else {
                    resultAmount += 16
                }
                updatePreview()
            }

            "set_amount_max" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                resultAmount = builder.getMaterial().maxStackSize
                updatePreview()
            }

            "remove_amount_1" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                resultAmount--
                updatePreview()
            }

            "remove_amount_16" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                resultAmount -= 16
                updatePreview()
            }

            "set_amount_min" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                resultAmount = 1
                updatePreview()
            }

            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                previousMenu.open()
            }
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (!InventoryClickLinkEvent.isLinkClick(event)) return

        event.currentItem
    }

    abstract fun createRecipe(): Recipe

    private fun updatePreview() {
        resultAmount = resultAmount.coerceIn(1, builder.getMaterial().maxStackSize)
        ResultPreview.render(
            inventory,
            builder.build(),
            resultAmount,
            5,
            0
        )
    }
}