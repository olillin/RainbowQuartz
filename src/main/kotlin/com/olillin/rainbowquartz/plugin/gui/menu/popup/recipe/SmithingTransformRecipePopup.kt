package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Ingredient
import com.olillin.rainbowquartz.craft.SmithingTransformRecipe
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

public class SmithingTransformRecipePopup(
    override val viewer: HumanEntity,
    override val placeholder: SmithingTransformRecipe?,
    override val previewItem: ItemStack,
    override val previousMenu: Menu?,
    override val callback: (SmithingTransformRecipe?) -> Unit,
) : GroupRecipePopup<SmithingTransformRecipe>() {

    override val recipeIcon: Material = SmithingTransformRecipe.ICON
    override val insertSlots: List<Int> = listOf(BASE_SLOT, ADDITION_SLOT, TEMPLATE_SLOT)

    init {
        if (placeholder != null) {
            insertItem(BASE_SLOT, placeholder.base.itemStack)
            insertItem(ADDITION_SLOT, placeholder.addition.itemStack)
            insertItem(TEMPLATE_SLOT, placeholder.template?.itemStack)
        }
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    public fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(BASE_LABEL_SLOT, ItemStack(Material.DIAMOND_CHESTPLATE).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    Component.text("Base")
                        .color(GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
                itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)
            }
        })
        inventory.setItem(ADDITION_LABEL_SLOT, ItemStack(Material.NETHERITE_INGOT).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    Component.text("Addition")
                        .color(GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
            }
        })
        inventory.setItem(TEMPLATE_LABEL_SLOT, ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    Component.text("Template")
                        .color(GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
                itemFlags.add(ItemFlag.HIDE_ARMOR_TRIM)
            }
        })

        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(20, EMPTY_PANEL)
        inventory.setItem(21, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
    }

    override fun createRecipe(): SmithingTransformRecipe {
        val base: Ingredient = Ingredient.fromItemStack(
            untransformItem(inventory.getItem(BASE_SLOT))
                ?: throw IllegalRecipeException("Base cannot be empty")
        )
        val addition: Ingredient = Ingredient.fromItemStack(
            untransformItem(inventory.getItem(ADDITION_SLOT))
                ?: throw IllegalRecipeException("Addition cannot be empty")
        )
        val template: Ingredient? = untransformItem(inventory.getItem(TEMPLATE_SLOT))
            ?.let { Ingredient.fromItemStack(it) }

        return SmithingTransformRecipe(base, addition, template)
            .setGroup(group)
            .setAmount(amount)
    }

    private companion object {
        const val BASE_SLOT: Int = 11
        const val BASE_LABEL_SLOT: Int = 2
        const val ADDITION_SLOT: Int = 12
        const val ADDITION_LABEL_SLOT: Int = 3
        const val TEMPLATE_SLOT: Int = 10
        const val TEMPLATE_LABEL_SLOT: Int = 1
    }
}