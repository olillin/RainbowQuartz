package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Recipe.Companion.asItemStack
import com.olillin.rainbowquartz.craft.ShapedRecipe
import com.olillin.rainbowquartz.item.rainbowQuartzId
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

class ShapedRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: ShapedRecipe?,
    override val result: ItemStack,
    override val previousMenu: Menu,
    override val callback: (ShapedRecipe?) -> Unit
) : GroupRecipePopup<ShapedRecipe>() {
//    override val title: Component = Component.text(if (placeholder == null) "New shaped recipe" else "Edit shaped recipe")
    override val recipeIcon: Material = ShapedRecipe.material

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
            val items = mutableListOf<ItemStack?>()
            for (line in padPattern(placeholder.getPattern())) {
                for (key in line) {
                    val ingredient = placeholder.getIngredient(key) ?: continue
                    items.add(asItemStack(ingredient).apply {
                        amount = 1
                    })
                }
            }
            grid = items.toTypedArray()

            group = placeholder.group
            amount = placeholder.amount
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
    }

    @Throws(IllegalStateException::class, NoSuchElementException::class)
    override fun createRecipe(): ShapedRecipe {
        if (grid.filterNotNull().isEmpty()) throw IllegalStateException("Grid cannot be empty")

        val validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        val ingredients: MutableMap<Char, ItemStack> = mutableMapOf()

        var shape = ""
        for (item in grid) {
            if (item == null) {
                shape += ' '
                continue
            }

            val key: Char = if (ingredients.containsValue(item)) {
                ingredients.entries.first { it.value == item }.key
            } else {
                val firstChar: Char = (item.itemMeta.rainbowQuartzId ?: item.type.key).key.uppercase()[0]
                if (ingredients.containsKey(firstChar)) {
                    validCharacters.firstOrNull { !ingredients.containsKey(it) }
                        ?: throw NoSuchElementException("Ran out of keys to assign to ingredients")
                } else {
                    firstChar
                }
            }
            ingredients[key] = item
            shape += key
        }

        val chunkedShape: MutableList<String> = shape.chunked(3).toMutableList()
        val pattern = trimPattern(chunkedShape)

        val result = ShapedRecipe(*pattern.toTypedArray())
        for (i in ingredients.entries) {
            result.setIngredient(i.key, i.value)
        }
        result.group = group
        result.amount = amount

        return result
    }

    /**
     * Trim pattern to minimum size.
     *
     * @see padPattern
     */
    private fun trimPattern(pattern: List<String>): List<String> {
        // Trim vertically
        val trimmedVertically = pattern.toMutableList().apply {
            while (first().isBlank()) {
                removeFirst()
            }
            while (last().isBlank()) {
                removeLast()
            }
        }
        // Trim horizontally
        val width = trimmedVertically.maxOf { it.trim().length }
        return pattern.map {
            it.trimStart().padStart(width)
                .trimEnd().padEnd(width)
        }.toMutableList()
    }

    /**
     * Pad pattern to fill grid.
     *
     * @see trimPattern
     */
    private fun padPattern(pattern: List<String>, width: Int = 3, height: Int = width): List<String> {
        return pattern.map {
            // Pad horizontally
            it.padEnd(width, ' ')
        }.toMutableList().apply {
            // Pad vertically
            while (size < height) {
                add(" ".repeat(width))
            }
        }
    }

    companion object {
        private val gridSlots = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
    }
}