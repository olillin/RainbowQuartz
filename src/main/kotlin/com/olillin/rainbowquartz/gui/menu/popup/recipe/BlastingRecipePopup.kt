package com.olillin.rainbowquartz.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.BlastingRecipe
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

public class BlastingRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: BlastingRecipe?,
    override val previewItem: ItemStack,
    override val previousMenu: Menu?,
    override val callback: (BlastingRecipe?) -> Unit
) : CookingRecipePopup<BlastingRecipe>() {

    override val recipeIcon: Material = BlastingRecipe.ICON
    override var cookTime: Int = 100

    init {
        if (placeholder != null) {
            exp = placeholder.exp
            cookTime = placeholder.cookTime
            amount = placeholder.amount
            insertItem(INPUT_SLOT, placeholder.input.itemStack)
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    public fun onOpen(event: InventoryOpenEvent) {
        val label = ItemStack(BlastingRecipe.ICON)
        label.itemMeta = label.itemMeta.apply {
            displayName(
                Component.text("Recipe input")
                    .color(GOLD)
                    .decoration(TextDecoration.ITALIC, false)
            )
        }
        insertItem(INPUT_LABEL_SLOT, label)
    }

    @Throws(IllegalRecipeException::class)
    override fun createRecipe(): BlastingRecipe {
        val input: Ingredient = Ingredient.fromItemStack(
            untransformItem(inventory.getItem(INPUT_SLOT))
                ?: throw IllegalRecipeException("Input cannot be empty")
        )
        return BlastingRecipe(input)
            .setGroup(group)
            .setAmount(amount)
            .setExp(exp)
            .setCookTime(cookTime)
    }
}