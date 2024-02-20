package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.craft.*
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.enchanted
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Paginator
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class EditItemRecipesMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) : EditItemMenu(viewer, builder) {
    var page = 0
    companion object {
        val recipeKeyLocation: NamespacedKey = NamespacedKey.fromString("rainbowquartz:recipe_key")!!
    }

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
                SelectRecipeTypeMenu(viewer, builder, this).open()
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

        val recipeKey: String = event.currentItem!!.itemMeta.persistentDataContainer
            .get(recipeKeyLocation, PersistentDataType.STRING) ?: return
        event.whoClicked.sendMessage(recipeKey)
    }

    private fun recipeItem(recipe: Recipe): ItemStack {
        val material: Material = when (recipe) {
            is ShapedRecipe, is ShapelessRecipe -> Material.CRAFTING_TABLE
            is FurnaceRecipe -> Material.FURNACE
            is SmokingRecipe -> Material.SMOKER
            is BlastingRecipe -> Material.BLAST_FURNACE
            is CampfireRecipe -> Material.CAMPFIRE
            is SmithingTransformRecipe -> Material.SMITHING_TABLE
            is StonecuttingRecipe -> Material.STONECUTTER
            else -> Material.BEDROCK
        }
        val item = ItemStack(material, 1)

        // Name and lore
        val meta = item.itemMeta
        meta.displayName(
            Component.text(recipe::class.simpleName!!)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
        )
        val key: String = recipe.key(builder.key).toString()
        meta.lore(listOf(
            Component.text(key)
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ))

        // Recipe key
        meta.persistentDataContainer.set(
            recipeKeyLocation,
            PersistentDataType.STRING,
            key
        )

        item.itemMeta = meta
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
}