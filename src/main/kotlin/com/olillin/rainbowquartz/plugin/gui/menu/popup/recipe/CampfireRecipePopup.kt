package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.CampfireRecipe
import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

public class CampfireRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: CampfireRecipe?,
    override val result: ItemStack,
    override val previousMenu: Menu?,
    override val callback: (CampfireRecipe?) -> Unit
) : CookingRecipePopup<CampfireRecipe>() {

    override val recipeIcon: Material = CampfireRecipe.ICON
    override var cookTime: Int = 600

    init {
        if (placeholder != null) {
            exp = placeholder.exp
            cookTime = placeholder.cookTime
            amount = placeholder.amount
            inventory.setItem(INPUT_SLOT, placeholder.input.itemStack)
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    public fun onOpen(event: InventoryOpenEvent) {
        val label = ItemStack(CampfireRecipe.ICON)
        label.itemMeta = label.itemMeta.apply {
            displayName(
                Component.text("Recipe input")
                    .color(GOLD)
                    .decoration(TextDecoration.ITALIC, false)
            )
        }
        inventory.setItem(INPUT_LABEL_SLOT, label)
    }

    @Throws(IllegalStateException::class)
    override fun createRecipe(): CampfireRecipe {
        val input: Ingredient = Ingredient.fromItemStack(
            untransformItem(inventory.getItem(INPUT_SLOT))
                ?: throw IllegalStateException("Input cannot be empty")
        )
        return CampfireRecipe(input)
            .setGroup(group)
            .setAmount(amount)
            .setExp(exp)
            .setCookTime(cookTime)
    }
}