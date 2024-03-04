package com.olillin.rainbowquartz.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.ShapelessRecipe
import com.olillin.rainbowquartz.gui.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

public class ShapelessRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: ShapelessRecipe?,
    override val previewItem: ItemStack,
    override val previousMenu: Menu,
    override val callback: (ShapelessRecipe?) -> Unit
) : GroupRecipePopup<ShapelessRecipe>() {

    //    override val title = Component.text(if (placeholder == null) "New shapeless recipe" else "Edit shapeless recipe")
    override val recipeIcon: Material = ShapelessRecipe.ICON

    override val insertSlots: List<Int>
        get() = gridSlots

    private var grid: Array<ItemStack?>
        get() = gridSlots.map { untransformItem(inventory.getItem(it)) }.toTypedArray()
        set(value) {
            if (value.size != 9) {
                throw IllegalArgumentException("Invalid value, grid must be of length 9")
            }
            gridSlots.forEachIndexed { index, slot ->
                insertItem(slot, value[index])
            }
        }

    init {
        if (placeholder != null) {
            val placeholderGrid: MutableList<ItemStack?> = placeholder.getIngredients()
                .map { it.itemStack }.toMutableList()
            amount = placeholder.amount
            while (placeholderGrid.size < 9) {
                placeholderGrid.add(null)
            }
            grid = placeholderGrid.toTypedArray()
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    public fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
    }

    @Throws(IllegalRecipeException::class)
    override fun createRecipe(): ShapelessRecipe {
        val ingredients: List<Ingredient> = grid.filterNotNull().map { Ingredient.fromItemStack(it) }
        if (ingredients.isEmpty()) throw IllegalRecipeException("Grid cannot be empty")

        val result = ShapelessRecipe()
        for (ingredient in ingredients) {
            result.addIngredient(ingredient)
        }
        result.group = group
        result.amount = amount

        return result
    }

    private companion object {
        val gridSlots = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
    }
}