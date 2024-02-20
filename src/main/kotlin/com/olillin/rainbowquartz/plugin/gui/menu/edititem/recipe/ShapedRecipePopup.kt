package com.olillin.rainbowquartz.plugin.gui.menu.edititem.recipe

import com.olillin.rainbowquartz.craft.ShapedRecipe
import com.olillin.rainbowquartz.item.rainbowQuartzId
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.InsertMenu
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import com.olillin.rainbowquartz.plugin.gui.menu.popup.Popup
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
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ShapedRecipePopup(
    override val viewer: HumanEntity,
    private val placeholder: ShapedRecipe?,
    private val result: ItemStack,
    override val previousMenu: Menu,
    override val callback: (ShapedRecipe?) -> Unit
) : InsertMenu(), Popup<ShapedRecipe?> {
    override var inventory: Inventory = Bukkit.createInventory(
        viewer,
        27,
        Component.text(if (placeholder == null) "New shaped recipe" else "Edit shaped recipe")
    )

    private var resultAmount = 1

    override val insertSlots: List<Int>
        get() = gridSlots

    private var grid: Array<ItemStack?>
        get() = gridSlots.map { untransformItem(inventory.getItem(it)) }.toTypedArray()
        set(value) {
            if (value.size != 9) {
                throw IllegalArgumentException("Invalid value, grid must be of length 9")
            }
            gridSlots.forEachIndexed { index, slot ->
                inventory.setItem(slot, value[index])
            }
        }

    init {
        if (placeholder != null) {
            val items = mutableListOf<ItemStack?>()
            padPattern(placeholder.getPattern()).forEach { line ->
                line.forEach { key ->
                    items.add(placeholder.getIngredient(key)?.itemStack?.apply {
                        amount = 1
                    })
                }
            }
            grid = items.toTypedArray()

            resultAmount = placeholder.getAmount()
        }
    }

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        inventory.setItem(0, EMPTY_PANEL)
        inventory.setItem(4, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
        inventory.setItem(9, EMPTY_PANEL)
        inventory.setItem(18, LinkItem.CANCEL)

        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(26, LinkItem.makeLink(
            "delete",
            Material.CAULDRON,
            Component.text("Delete recipe").color(NamedTextColor.RED)
        ))

        updatePreview()
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "submit" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                val recipe = createRecipe()
                callback(recipe)
                if (activeViewers().contains(viewer)) {
                    previousMenu.open()
                }
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
                resultAmount = result.type.maxStackSize
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

            "cancel" -> back()

            "delete" -> {
                viewer.playSound(Sound.ITEM_BUCKET_EMPTY)
                callback(null)
                if (activeViewers().contains(viewer))
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
            meta.lore(
                listOf(
                    Component.text("Pattern cannot be empty")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                )
            )
            item.itemMeta = meta
            inventory.setItem(CREATE_SLOT, item)
        } else {
            inventory.setItem(CREATE_SLOT, CREATE_BUTTON)
        }
    }

    private fun createRecipe(): ShapedRecipe {
        val validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
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

        val chunkedShape: MutableList<String> = shape.chunked(3).toMutableList()
        val pattern = trimPattern(chunkedShape)

        val result = ShapedRecipe(*pattern.toTypedArray())
        for (i in ingredients.entries) {
            result.setIngredient(i.key, i.value)
        }
        result.setAmount(resultAmount)

        return result
    }

    /**
     * Trim pattern to minimum size.
     *
     * @see padPattern
     */
    private fun trimPattern(pattern: List<String>): List<String> {
        // Trim vertically
        val trimmedVertically = pattern.toMutableList().apply {
            while (first().isBlank()) {
                removeFirst()
            }
            while (last().isBlank()) {
                removeLast()
            }
        }
        // Trim horizontally
        val width = trimmedVertically.maxOf { it.trim().length }
        return pattern.map {
            it.trimStart().padStart(width)
                .trimEnd().padEnd(width)
        }.toMutableList()
    }

    /**
     * Pad pattern to fill grid.
     *
     * @see trimPattern
     */
    private fun padPattern(pattern: List<String>, width: Int = 3, height: Int = width): List<String> {
        return pattern.map {
            // Pad horizontally
            it.padEnd(width, ' ')
        }.toMutableList().apply {
            // Pad vertically
            while (size < height) {
                add(" ".repeat(width))
            }
        }
    }

    private fun updatePreview() {
        resultAmount = resultAmount.coerceIn(1, result.type.maxStackSize)
        ResultPreview.render(
            inventory,
            result,
            resultAmount,
            5,
            0
        )
    }

    companion object {
        private val gridSlots = listOf(1, 2, 3, 10, 11, 12, 19, 20, 21)

        private const val CREATE_SLOT = 17
        private val CREATE_BUTTON = LinkItem.makeLink(
            "submit",
            Material.CRAFTING_TABLE,
            Component.text("Create recipe").color(NamedTextColor.GREEN)
        )
    }
}