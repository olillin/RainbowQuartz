package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.ShapedRecipe
import com.olillin.rainbowquartz.item.rainbowQuartzId
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

public class ShapedRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: ShapedRecipe?,
    override val previewItem: ItemStack,
    override val previousMenu: Menu,
    override val callback: (ShapedRecipe?) -> Unit,
) : GroupRecipePopup<ShapedRecipe>() {

    //    override val title: Component = Component.text(if (placeholder == null) "New shaped recipe" else "Edit shaped recipe")
    override val recipeIcon: Material = ShapedRecipe.ICON

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
            val items = Array<ItemStack?>(9) { null }
            ShapedRecipe.padPattern(placeholder.getPattern()).forEachIndexed line@{ y, line ->
                line.forEachIndexed char@{ x, char ->
                    val ingredient = placeholder.getIngredient(char) ?: return@char
                    items[y * 3 + x] = ingredient.itemStack
                }
            }
            grid = items

            amount = placeholder.amount
            group = placeholder.group
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    public fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
    }

    @Throws(IllegalRecipeException::class, NoSuchElementException::class)
    override fun createRecipe(): ShapedRecipe {
        if (grid.filterNotNull().isEmpty()) throw IllegalRecipeException("Grid cannot be empty")

        val validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        val ingredients: MutableMap<Char, Ingredient> = mutableMapOf()

        var shape = ""
        for (item in grid) {
            if (item == null) {
                shape += ' '
                continue
            }

            val key: Char = if (ingredients.values.any { it.toRecipeChoice().test(item) }) {
                ingredients.entries.first { it.value.toRecipeChoice().test(item) }.key
            } else {
                val firstChar: Char = (item.itemMeta.rainbowQuartzId ?: item.type.key).key.uppercase()[0]
                if (ingredients.containsKey(firstChar)) {
                    validCharacters.firstOrNull { !ingredients.containsKey(it) }
                        ?: throw NoSuchElementException("Ran out of keys to assign to ingredients")
                } else {
                    firstChar
                }
            }
            ingredients[key] = Ingredient.fromItemStack(item)
            shape += key
        }

        val chunkedShape: MutableList<String> = shape.chunked(3).toMutableList()
        val pattern = ShapedRecipe.trimPattern(chunkedShape)

        val result = ShapedRecipe(*pattern.toTypedArray())
        for (i in ingredients.entries) {
            result.setIngredient(i.key, i.value)
        }
        result.group = group
        result.amount = amount

        return result
    }

    private companion object {
        val gridSlots = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
    }
}