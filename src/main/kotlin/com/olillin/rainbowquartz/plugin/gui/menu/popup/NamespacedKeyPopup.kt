package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.ITALIC
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/** A [TextPopup] menu that provides a [NamespacedKey] */
class NamespacedKeyPopup(
        override val viewer: HumanEntity, private val placeholder: NamespacedKey? = null, private val defaultNamespace: String = "rainbowquartz", override val previousMenu: Menu?, override val callback: (NamespacedKey) -> Unit
) : TextPopup<NamespacedKey>(viewer, placeholder, previousMenu, callback) {
    override fun firstItem(placeholder: NamespacedKey?): ItemStack {
        val stack = ItemStack(if (placeholder == null) Material.MAP else Material.FILLED_MAP)
        stack.itemMeta = stack.itemMeta.apply {
                displayName(Component.text(placeholder?.toString() ?: "").color(YELLOW).decoration(ITALIC, false))
                lore(listOf(
                        Component.text("↑ Previous id ↑").color(GRAY).decoration(ITALIC, false)
                ))
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
            }
        return stack
    }

    override fun resultItem(input: String?): ItemStack {
        val key: NamespacedKey? = parseInput(input)
        return if (key != null) {
            ItemStack(Material.FILLED_MAP).apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("Submit").color(GREEN))
                    lore(listOf(
                        Component.text("New id: ")
                                .append(Component.text(key.toString()).color(WHITE))
                    ))
                }
            }
        } else {
            INVALID_INPUT
        }
    }

    override fun parseInput(input: String?): NamespacedKey? {
        var text: String = (input?.trim().takeIf { it?.isNotEmpty() == true })
                ?: (placeholder?.toString().takeIf { it?.isNotEmpty() == true })
                ?: return null
        if (!text.matches(Regex("^([0-9a-z_.-]+:)?[0-9a-z_.-]+(/[0-9a-z_.-]+)*$"))) {
            // Invalid resource location
            return null
        }
        if (!text.contains(':')) {
            // No namespace provided
            text = "$defaultNamespace:$text"
        }
        return NamespacedKey.fromString(text)
    }

    @EventHandler
    override fun onClickTextPopup(event: InventoryClickEvent) {
        if (event.slotType != InventoryType.SlotType.RESULT) return

        val key: NamespacedKey? = parseInput(inventory.renameText)
        if (key == null) {
            // Input is invalid
            viewer.sendMessage(Component.text("Input is not a valid key").color(RED))
            viewer.playSound(Sound.BLOCK_ANVIL_PLACE)
            return
        }

        viewer.playSound(Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT)

        callback(key)
        if (activeViewers().contains(viewer)) {
            back()
        }
    }

    companion object {
        val INVALID_INPUT: ItemStack = LinkItem.makeLink(
            "submit",
            Material.BARRIER,
            Component.text("Input is invalid").color(RED)
        )
    }
}