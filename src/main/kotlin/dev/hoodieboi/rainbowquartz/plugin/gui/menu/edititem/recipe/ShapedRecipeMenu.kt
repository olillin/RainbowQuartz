package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.recipe

import dev.hoodieboi.rainbowquartz.craft.ShapedRecipe
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.InsertMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.SelectRecipeTypeMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ShapedRecipeMenu(
    override val viewer: HumanEntity,
    private val builder: ItemBuilder,
    override val previousMenu: SelectRecipeTypeMenu
) : InsertMenu(), RecipeMenu<ShapedRecipe> {
    override var inventory: Inventory = Bukkit.createInventory(viewer, 27, Component.text("New shaped recipe"))

    private var resultAmount = 1

    override val insertSlots: List<Int>
        get() = gridSlots

    private val grid: List<ItemStack?>
        get() = gridSlots.map { untransformItem(inventory.getItem(it)) }

    companion object {
        private const val PREVIEW_SLOT = 15
        private val gridSlots: List<Int>
            = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)

        private const val CREATE_SLOT = 17
        private val CREATE_BUTTON = LinkItem.makeLink(
            "submit",
            Material.CRAFTING_TABLE,
            Component.text("Create recipe").color(NamedTextColor.GREEN)
        )
    }

    init {
        inventory.setItem(0, EMPTY_PANEL)
        inventory.setItem(4, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(18, LinkItem.BACK)

        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(26, EMPTY_PANEL)

        updatePreview()
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "submit" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                val recipe = createRecipe()
                builder.addRecipe(recipe)
                previousMenu.open()
            }

            "add_amount_1" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                resultAmount++
                updatePreview()
            }

            "add_amount_16" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                if (resultAmount == 1) {
                    resultAmount = 16
                } else {
                    resultAmount += 16
                }
                updatePreview()
            }

            "set_amount_max" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                resultAmount = builder.getMaterial().maxStackSize
                updatePreview()
            }

            "remove_amount_1" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                resultAmount--
                updatePreview()
            }

            "remove_amount_16" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                resultAmount -= 16
                updatePreview()
            }

            "set_amount_min" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                resultAmount = 1
                updatePreview()
            }

            "back" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF)
                previousMenu.open()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onChange(event: InventoryEvent) {
        if (grid.all { it == null }) {
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta
            meta.displayName(
                Component.text("Cannot create recipe")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false)
            )
            meta.lore(listOf(
                Component.text("Pattern cannot be empty")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            ))
            item.itemMeta = meta
            inventory.setItem(CREATE_SLOT, item)
        } else {
            inventory.setItem(CREATE_SLOT, CREATE_BUTTON)
        }
    }

    override fun createRecipe(): ShapedRecipe {
        val validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val ingredients: MutableMap<Char, ItemStack> = mutableMapOf()

        var shape = ""
        for (item in grid) {
            if (item == null) {
                shape += ' '
                continue
            }

            val key: Char = if (ingredients.containsValue(item)) {
                ingredients.entries.first { it.value == item }.key
            } else {
                val firstChar: Char = (item.itemMeta.rainbowQuartzId ?: item.type.key).key.uppercase()[0]
                if (ingredients.containsKey(firstChar)) {
                    validCharacters.firstOrNull { !ingredients.containsKey(it) }
                        ?: throw NoSuchElementException("Could not assign key to ingredient")
                } else {
                    firstChar
                }
            }
            ingredients[key] = item
            shape += key
        }

        var chunkedShape: MutableList<String> = shape.chunked(3).toMutableList()
        // Crop vertically
        while (chunkedShape.first().isBlank()) {
            chunkedShape.removeFirst()
        }
        while (chunkedShape.last().isBlank()) {
            chunkedShape.removeLast()
        }
        // Crop horizontally
        Bukkit.getLogger().info(chunkedShape.joinToString())
        val width = chunkedShape.map { it.trim() }.maxOf { it.length }
        chunkedShape = chunkedShape.map {
            it.trimStart().padStart(width)
        }.map {
            it.trimEnd().padEnd(width)
        }.toMutableList()

        val result = ShapedRecipe(*chunkedShape.toTypedArray())
        for (i in ingredients.entries) {
            result.setIngredient(i.key, i.value)
        }

        return result
    }

    private fun updatePreview() {
        resultAmount = resultAmount.coerceIn(1, builder.getMaterial().maxStackSize)
        ResultPreview.render(
            inventory,
            builder.build(),
            resultAmount,
            5,
            0
        )
    }
}