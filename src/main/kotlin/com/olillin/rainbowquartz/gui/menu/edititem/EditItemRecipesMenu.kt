package com.olillin.rainbowquartz.gui.menu.edititem

import com.olillin.rainbowquartz.craft.*
import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.gui.LinkItem
import com.olillin.rainbowquartz.gui.enchanted
import com.olillin.rainbowquartz.gui.menu.Menu
import com.olillin.rainbowquartz.gui.menu.Paginator
import com.olillin.rainbowquartz.gui.menu.playSound
import com.olillin.rainbowquartz.gui.menu.popup.recipe.*
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

internal class EditItemRecipesMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) :
    EditItemMenu(viewer, builder) {

    private var page: Int = 0

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(RECIPES_SLOT, inventory.getItem(RECIPES_SLOT)?.enchanted())

        inventory.setItem(
            8, LinkItem.makeLink(
                "add_recipe",
                Material.NETHER_STAR,
                Component.text("New Recipe").color(NamedTextColor.AQUA),
                listOf(
                    Component.text("Create a new recipe")
                )
            )
        )
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
        val oldRecipe = builder.getRecipe(recipeKey) ?: return
        if (event.click == ClickType.LEFT) {
            when (oldRecipe) {
                is ShapedRecipe -> ShapedRecipePopup(viewer, oldRecipe, builder.build().getItem(), this) {
                    updateRecipe(
                        oldRecipe,
                        it
                    )
                }.open()

                is ShapelessRecipe -> ShapelessRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()

                is FurnaceRecipe -> FurnaceRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()

                is SmokingRecipe -> SmokingRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()

                is BlastingRecipe -> BlastingRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()

                is CampfireRecipe -> CampfireRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()

                is SmithingTransformRecipe -> SmithingTransformRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()

                is StonecuttingRecipe -> StonecuttingRecipePopup(
                    viewer,
                    oldRecipe,
                    builder.build().getItem(),
                    this
                ) { updateRecipe(oldRecipe, it) }.open()
            }
        } else if (event.click == ClickType.RIGHT) {
            viewer.playSound(Sound.ITEM_BUCKET_EMPTY)
            builder.removeRecipe(oldRecipe)
            renderPaginator()
        }
    }

    private fun updateRecipe(oldRecipe: Recipe<*, *>, newRecipe: Recipe<*, *>?) {
        if (newRecipe == null) {
            builder.removeRecipe(oldRecipe)
        } else {
            builder.removeRecipe(oldRecipe)
            builder.addRecipe(newRecipe)
        }
        renderPaginator()
    }

    private fun recipeItem(recipe: Recipe<*, *>): ItemStack {
        val material: Material = when (recipe) {
            is ShapedRecipe -> ShapedRecipe.ICON
            is ShapelessRecipe -> ShapelessRecipe.ICON
            is FurnaceRecipe -> FurnaceRecipe.ICON
            is SmokingRecipe -> SmokingRecipe.ICON
            is BlastingRecipe -> BlastingRecipe.ICON
            is CampfireRecipe -> CampfireRecipe.ICON
            is SmithingTransformRecipe -> SmithingTransformRecipe.ICON
            is StonecuttingRecipe -> StonecuttingRecipe.ICON
            else -> Material.CRAFTING_TABLE
        }
        val item = ItemStack(material).apply {
            itemMeta = itemMeta.apply {
                val key: String = recipe.key(builder.id).toString()
                // Name and lore
                displayName(
                    Component.text(recipe::class.simpleName!!)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
                lore(
                    listOf(
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
                    )
                )

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

    private companion object {
        val recipeKeyLocation: NamespacedKey = NamespacedKey.fromString("rainbowquartz:recipe_key")!!
    }
}