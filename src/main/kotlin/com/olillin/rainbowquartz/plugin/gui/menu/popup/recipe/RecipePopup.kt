package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.InsertMenu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import com.olillin.rainbowquartz.plugin.gui.menu.popup.Popup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class RecipePopup<T>: InsertMenu(), Popup<T?> {
    protected abstract val result: ItemStack
    protected var resultAmount: Int = 1
    protected abstract val placeholder: T?

    protected open val title: Component = Component.text(
        if (placeholder == null) {
            "New recipe"
        } else {
            "Edit recipe"
        }
    )
    protected open val recipeIcon: Material = Material.CRAFTING_TABLE

    override var inventory: Inventory = Bukkit.createInventory(
        viewer,
        27,
        title
    )

    abstract fun createRecipe(): T

    @EventHandler(priority = EventPriority.HIGH)
    fun onOpenRecipePopup(event: InventoryOpenEvent) {
        inventory.setItem(0, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(18, LinkItem.BACK)

        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(26, LinkItem.makeLink(
            "delete",
            Material.CAULDRON,
            Component.text("Delete recipe").color(NamedTextColor.RED)
        ))

        updatePreview()
    }

    @EventHandler
    fun onLinkRecipePopup(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "submit" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                val recipe = createRecipe()
                callback(recipe)
                if (activeViewers().contains(viewer)) {
                    previousMenu?.open()
                }
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
                resultAmount = result.type.maxStackSize
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
                back()
            }

            "delete" -> {
                viewer.playSound(Sound.ITEM_BUCKET_EMPTY)
                callback(null)
                if (activeViewers().contains(viewer))
                    previousMenu?.open()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onChangeRecipePopup(event: InventoryEvent) {
        try {
            createRecipe()
            inventory.setItem(CREATE_SLOT, LinkItem.makeLink(
                "submit",
                recipeIcon,
                Component.text("Create recipe").color(NamedTextColor.GREEN)
            ))
        } catch (e: RuntimeException) {
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta
            meta.displayName(
                Component.text("Cannot create recipe")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false)
            )
            val message = e.message
            if (message != null) {
                meta.lore(
                    listOf(
                        Component.text(message)
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
                    )
                )
            }
            item.itemMeta = meta
            inventory.setItem(CREATE_SLOT, item)
        }
    }

    protected fun updatePreview() {
        resultAmount = resultAmount.coerceIn(1, result.type.maxStackSize)
        ResultPreview.render(
            inventory,
            result,
            resultAmount,
            5,
            0
        )
    }

    companion object {
        protected const val CREATE_SLOT: Int = 17
    }
}