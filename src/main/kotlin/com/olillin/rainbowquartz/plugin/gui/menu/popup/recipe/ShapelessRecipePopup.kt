package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.ShapelessRecipe
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

class ShapelessRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: ShapelessRecipe?,
    override val result: ItemStack,
    override val previousMenu: Menu,
    override val callback: (ShapelessRecipe?) -> Unit
) : RecipePopup<ShapelessRecipe>() {
    //    override val title = Component.text(if (placeholder == null) "New shapeless recipe" else "Edit shapeless recipe")
    override val recipeIcon = ShapelessRecipe.material

    override val insertSlots: List<Int>
        get() = gridSlots

    private var grid: Array<ItemStack?>
        get() = gridSlots.map { untransformItem(inventory.getItem(it)) }.toTypedArray()
        set(value) {
            if (value.size != 9) {
                throw IllegalArgumentException("Invalid value, grid must be of length 9")
            }
            gridSlots.forEachIndexed { index, slot ->
                inventory.setItem(slot, value[index])
            }
        }

    init {
        if (placeholder != null) {
            val placeholderGrid: MutableList<ItemStack?> = placeholder.getIngredients()
                .map { it.itemStack.apply { amount = 1 } }.toMutableList()
            resultAmount = placeholder.getAmount()
            while (placeholderGrid.size < 9) {
                placeholderGrid.add(null)
            }
            grid = placeholderGrid.toTypedArray()
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(4, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
    }

    @Throws(IllegalStateException::class)
    override fun createRecipe(): ShapelessRecipe {
        val ingredients: List<ItemStack> = grid.filterNotNull()
        if (ingredients.isEmpty()) throw IllegalStateException("Grid cannot be empty")

        val result = ShapelessRecipe()
        for (ingredient in ingredients) {
            result.addIngredient(ingredient)
        }
        result.setAmount(resultAmount)

        return result
    }

    companion object {
        private val gridSlots = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
    }
}