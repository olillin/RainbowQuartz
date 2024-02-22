package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.BlastingRecipe
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

class BlastingRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: BlastingRecipe?,
    override val result: ItemStack,
    override val previousMenu: Menu?,
    override val callback: (BlastingRecipe?) -> Unit
) : CookingRecipePopup<BlastingRecipe>() {
    override val recipeIcon: Material = BlastingRecipe.material
    override var cookTime = 100

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
        val label = ItemStack(BlastingRecipe.material)
        label.itemMeta = label.itemMeta.apply {
            displayName(Component.text("Recipe input")
                .color(GOLD)
                .decoration(TextDecoration.ITALIC, false)
            )
        }
        inventory.setItem(INPUT_LABEL_SLOT, label)
    }

    @Throws(IllegalStateException::class)
    override fun createRecipe(): BlastingRecipe {
        val input: ItemStack = untransformItem(inventory.getItem(INPUT_SLOT)) ?: throw IllegalStateException("Input cannot be empty")
        return BlastingRecipe(input)
            .setGroup(group)
            .setAmount(amount)
            .setExp(exp)
            .setCookTime(cookTime) as BlastingRecipe
    }
}