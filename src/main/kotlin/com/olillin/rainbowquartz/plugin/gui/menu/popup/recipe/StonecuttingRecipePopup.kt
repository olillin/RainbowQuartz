package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.StonecuttingRecipe
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

public class StonecuttingRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: StonecuttingRecipe?,
    override val previewItem: ItemStack,
    override val previousMenu: Menu?,
    override val callback: (StonecuttingRecipe?) -> Unit,
) : GroupRecipePopup<StonecuttingRecipe>() {

    override val recipeIcon: Material = StonecuttingRecipe.ICON
    override val insertSlots: List<Int> = listOf(INPUT_SLOT)

    init {
        if (placeholder != null) {
            insertItem(INPUT_SLOT, placeholder.input.itemStack)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @EventHandler
    public fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(INPUT_LABEL_SLOT, ItemStack(StonecuttingRecipe.ICON).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    Component.text("Input")
                        .color(GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
            }
        })

        inventory.setItem(1, EMPTY_PANEL)
        inventory.setItem(2, EMPTY_PANEL)
        inventory.setItem(3, EMPTY_PANEL)
        inventory.setItem(12, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(20, EMPTY_PANEL)
        inventory.setItem(21, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
    }

    override fun createRecipe(): StonecuttingRecipe {
        val input: Ingredient = Ingredient.fromItemStack(
            untransformItem(inventory.getItem(INPUT_SLOT))
                ?: throw IllegalRecipeException("Input cannot be empty")
        )

        return StonecuttingRecipe(input)
            .setGroup(group)
            .setAmount(amount)
    }

    private companion object {
        const val INPUT_SLOT: Int = 11
        const val INPUT_LABEL_SLOT: Int = 10
    }
}