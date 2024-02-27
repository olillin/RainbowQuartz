package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.FurnaceRecipe
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.Recipe.Companion.asItemStack
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

class FurnaceRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: FurnaceRecipe?,
    override val result: ItemStack,
    override val previousMenu: Menu?,
    override val callback: (FurnaceRecipe?) -> Unit
) : CookingRecipePopup<FurnaceRecipe>() {
    override val recipeIcon: Material = FurnaceRecipe.material

    init {
        if (placeholder != null) {
            exp = placeholder.exp
            cookTime = placeholder.cookTime
            amount = placeholder.amount
            inventory.setItem(INPUT_SLOT, asItemStack(placeholder.input).also {
                it.amount = 1
            })
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        val label = ItemStack(FurnaceRecipe.material)
        label.itemMeta = label.itemMeta.apply {
            displayName(Component.text("Recipe input")
                .color(GOLD)
                .decoration(TextDecoration.ITALIC, false)
            )
        }
        inventory.setItem(INPUT_LABEL_SLOT, label)
    }

    @Throws(IllegalStateException::class)
    override fun createRecipe(): FurnaceRecipe {
        val input: Ingredient = Ingredient.fromItemStack(
            untransformItem(inventory.getItem(INPUT_SLOT))
                ?: throw IllegalStateException("Input cannot be empty")
        )
        return FurnaceRecipe(input)
            .setGroup(group)
            .setAmount(amount)
            .setExp(exp)
            .setCookTime(cookTime) as FurnaceRecipe
    }
}