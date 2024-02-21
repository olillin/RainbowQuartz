package com.olillin.rainbowquartz.plugin.gui.menu.edititem

import com.olillin.rainbowquartz.craft.*
import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.enchanted
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.Paginator
import com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe.ShapedRecipePopup
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe.ChoiceRecipePopup
import com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe.ShapelessRecipePopup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class EditItemRecipesMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) : EditItemMenu(viewer, builder) {
    var page = 0

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(RECIPES_SLOT, inventory.getItem(RECIPES_SLOT)?.enchanted())

        inventory.setItem(8, LinkItem.makeLink(
            "add_recipe",
            Material.NETHER_STAR,
            Component.text("New Recipe").color(NamedTextColor.AQUA),
            listOf(
                Component.text("Create a new recipe")
            )
        ))
        inventory.setItem(17, EMPTY_PANEL)
        inventory.setItem(26, EMPTY_PANEL)

        renderPaginator()
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "add_recipe" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                ChoiceRecipePopup(viewer, builder.build().getItem(), this) {
                    builder.addRecipe(it)
                }.open()
            }
            "previous_page" -> {
                page--
                renderPaginator()
                viewer.playSound(Sound.ITEM_BOOK_PAGE_TURN)
            }
            "next_page" -> {
                page++
                renderPaginator()
                viewer.playSound(Sound.ITEM_BOOK_PAGE_TURN)
            }
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (!InventoryClickLinkEvent.isLinkClick(event)) return

        val recipeKeyString: String = event.currentItem!!.itemMeta.persistentDataContainer
            .get(recipeKeyLocation, PersistentDataType.STRING) ?: return
        val (namespace, value) = recipeKeyString.split(':').toTypedArray()
        val recipeKey = NamespacedKey(namespace, value)
        val recipe = builder.getRecipe(recipeKey)
        if (event.click == ClickType.LEFT) {
            when (recipe) {
                is ShapedRecipe -> ShapedRecipePopup(viewer, recipe, builder.build().getItem(), this) {
                    if (it == null) {
                        builder.removeRecipe(recipe)
                    } else {
                        builder.removeRecipe(recipe)
                        builder.addRecipe(it)
                    }
                    renderPaginator()
                }.open()
                is ShapelessRecipe -> ShapelessRecipePopup(viewer, recipe, builder.build().getItem(), this) {
                    if (it == null) {
                        builder.removeRecipe(recipe)
                    } else {
                        builder.removeRecipe(recipe)
                        builder.addRecipe(it)
                    }
                    renderPaginator()
                }.open()
            }
        } else if (event.click == ClickType.RIGHT) {
            viewer.playSound(Sound.ITEM_BUCKET_EMPTY)
            builder.removeRecipe(recipe)
            renderPaginator()
        }
    }

    private fun recipeItem(recipe: Recipe): ItemStack {
        val material: Material = when (recipe) {
            is ShapedRecipe -> ShapedRecipe.material
            is ShapelessRecipe -> ShapelessRecipe.material
            is FurnaceRecipe -> FurnaceRecipe.material
            is SmokingRecipe -> SmokingRecipe.material
            is BlastingRecipe -> BlastingRecipe.material
            is CampfireRecipe -> CampfireRecipe.material
            is SmithingTransformRecipe -> SmithingTransformRecipe.material
            is StonecuttingRecipe -> StonecuttingRecipe.material
            else -> Material.BEDROCK
        }
        val item = ItemStack(material).apply {
            itemMeta = itemMeta.apply {
                val key: String = recipe.key(builder.key).toString()
                // Name and lore
                displayName(
                    Component.text(recipe::class.simpleName!!)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
                lore(listOf(
                    Component.text(key)
                        .color(NamedTextColor.DARK_PURPLE)
                        .decoration(TextDecoration.ITALIC, false),
                    Component.translatable("key.mouse.left")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(" to edit")),
                    Component.translatable("key.mouse.right")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(" to delete"))
                ))

                // Recipe key
                persistentDataContainer.set(
                    recipeKeyLocation,
                    PersistentDataType.STRING,
                    key
                )
            }
        }
        return item
    }

    private fun renderPaginator() {
        Paginator.render(
            inventory,
            builder.recipes(),
            { recipeItem(it) },
            page,
            5,
            3,
            3,
            0
        )
    }

    companion object {
        val recipeKeyLocation: NamespacedKey = NamespacedKey.fromString("rainbowquartz:recipe_key")!!
    }
}