package com.olillin.rainbowquartz.plugin.gui.menu

import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.item.ItemBuilder
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.edititem.EditItemGeneralMenu
import com.olillin.rainbowquartz.plugin.gui.menu.popup.MaterialPopup
import com.olillin.rainbowquartz.plugin.gui.menu.popup.NamespacedKeyPopup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

internal class NewItemMenu(override val viewer: HumanEntity, override val previousMenu: Menu?) : ImmutableMenu() {
    override val inventory: Inventory = Bukkit.createInventory(viewer, 27, Component.text("New item"))

    private var id: NamespacedKey? = null
    private var material: Material? = null

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onOpen(event: InventoryOpenEvent) {
        inventory.fill(EMPTY_PANEL)

        var lore: MutableList<Component> = mutableListOf()
        if (id == null) {
            lore.add(Component.text("No id selected").color(RED))
        } else {
            lore.add(Component.text("Current id"))
            lore.add(
                Component.text(" ")
                    .append(Component.text(id?.toString() ?: "").color(AQUA))
            )
        }
        inventory.setItem(
            11, LinkItem.makeLink(
                "set_id",
                Material.NAME_TAG,
                Component.text("Set item id").color(LIGHT_PURPLE),
                lore
            )
        )

        lore = mutableListOf()
        if (material == null) {
            lore.add(Component.text("No material selected").color(RED))
        } else {
            lore.add(Component.text("Current material"))
            lore.add(
                Component.text(" ")
                    .append(Component.translatable(material!!).color(YELLOW))
            )
        }
        inventory.setItem(
            12, LinkItem.makeLink(
                "set_material",
                material ?: Material.IRON_INGOT,
                Component.text("Set material").color(LIGHT_PURPLE),
                lore
            )
        )

        inventory.setItem(17, invalidInputMessage(id, material)?.let { message ->
            LinkItem.makeLink(
                "complete",
                Material.BARRIER,
                Component.text("Cannot create item").color(RED),
                listOf(
                    Component.text(message).color(GRAY)
                )
            )
        } ?: LinkItem.SUBMIT)

        inventory.setItem(18, LinkItem.BACK)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "set_id" -> {
                NamespacedKeyPopup(viewer, placeholder = id, previousMenu = this) { key ->
                    id = key
                }.open()
            }

            "set_material" -> {
                MaterialPopup(viewer, material, previousMenu = this) { material ->
                    this.material = material
                }.open()
            }

            "submit" -> {
                // Check input
                val key = id
                val itemMaterial = material
                invalidInputMessage(key, itemMaterial)?.let { message ->
                    viewer.sendMessage(
                        Component.text("Cannot create item, ").color(RED)
                            .append(Component.text(message.lowercase()))
                    )
                    return@onLink
                }
                // Create item
                val builder = ItemBuilder(key!!, itemMaterial!!)
                EditItemGeneralMenu(viewer, builder, this).open()
            }

            "back" -> {
                back()
            }
        }
    }

    /**
     * Validates input and returns a message explaining the issue or `null` if there is none.
     *
     * @param id The id to validate
     * @param material The material to validate
     */
    private fun invalidInputMessage(id: NamespacedKey?, material: Material?): String? {
        return if (id == null) {
            "No id has been chosen"
        } else if (material == null) {
            "No material has been chosen"
        } else if (!material.isItem) {
            "$material is not a valid material"
        } else if (RainbowQuartz.itemManager.containsItem(id)) {
            "An item with the id $id already exists"
        } else null
    }
}