package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.InsertMenu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import com.olillin.rainbowquartz.plugin.gui.menu.popup.Popup
import net.kyori.adventure.text.Component
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

public abstract class RecipePopup<T: Recipe<*, *>> : InsertMenu(), Popup<T?> {
    protected abstract val result: ItemStack
    protected var amount: Int = 1
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

    public abstract fun createRecipe(): T

    @EventHandler(priority = EventPriority.HIGH)
    public fun onOpenRecipePopup(event: InventoryOpenEvent) {
        inventory.setItem(0, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(18, LinkItem.BACK)

        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(
            26, LinkItem.makeLink(
                "delete",
                Material.CAULDRON,
                Component.text("Delete recipe").color(NamedTextColor.RED)
            )
        )

        updatePreview()
    }

    @EventHandler
    public fun onLinkRecipePopup(event: InventoryClickLinkEvent) {
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
                amount++
                updatePreview()
            }

            "add_amount_16" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                if (amount == 1) {
                    amount = 16
                } else {
                    amount += 16
                }
                updatePreview()
            }

            "set_amount_max" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                amount = result.type.maxStackSize
                updatePreview()
            }

            "remove_amount_1" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                amount--
                updatePreview()
            }

            "remove_amount_16" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                amount -= 16
                updatePreview()
            }

            "set_amount_min" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                amount = 1
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
    public fun onChangeRecipePopup(event: InventoryEvent) {
        try {
            createRecipe()
            inventory.setItem(
                CREATE_SLOT, LinkItem.makeLink(
                    "submit",
                    recipeIcon,
                    Component.text("Create recipe").color(NamedTextColor.GREEN)
                )
            )
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
        amount = amount.coerceIn(1, result.type.maxStackSize)
        ResultPreview.render(
            inventory,
            result,
            amount,
            5,
            0
        )
    }

    protected companion object {
        public const val CREATE_SLOT: Int = 17
    }
}