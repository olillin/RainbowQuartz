package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.EditItemGeneralMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.popup.NamespacedKeyPopup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

class NewItemMenu(override val viewer: HumanEntity, override val previousMenu: Menu?) : ImmutableMenu() {
    override val inventory: Inventory = Bukkit.createInventory(viewer, 27, Component.text("New item"))

    private var id: NamespacedKey? = null
    private var material: Material? = null

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        inventory.fill(EMPTY_PANEL)

        inventory.setItem(11, LinkItem.makeLink("set_id",
            Material.NAME_TAG,
            Component.text("Set item id").color(LIGHT_PURPLE),
            listOf(
                Component.text("Current id"),
                Component.text(" ").append(Component.text(id?.toString() ?: "").color(AQUA))
            )
        ))

        inventory.setItem(12, LinkItem.makeLink("set_material",
            material ?: Material.IRON_INGOT,
            Component.text("Set material").color(LIGHT_PURPLE),
            listOf(
                Component.text("Current material"),
                Component.text(" ").append(Component.text(material?.name ?: "").color(YELLOW))
            )
        ))

        inventory.setItem(17, invalidInputMessage(id, material)?.let{message -> LinkItem.makeLink(
            "complete",
            Material.BARRIER,
            Component.text("Cannot create item").color(RED),
            listOf(
                Component.text(message).color(GRAY)
            )
        )} ?: LinkItem.SUBMIT)

        inventory.setItem(18, LinkItem.BACK)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "set_id" -> {
                NamespacedKeyPopup(viewer, id, this) {key ->
                    id = key
                }.open()
            }
            "set_material" -> {
                viewer.sendMessage("set_material")
            }
            "complete" -> {
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
     * Validates input and returns a message explaining the issue
     *
     * @param key The key to validate
     * @param material The material to validate
     * @return a message describing the issue, or null if the input is valid
     */
    private fun invalidInputMessage(key: NamespacedKey?, material: Material?): String? {
        return if (key == null) {
            "No key has been chosen"
        } else if (material == null) {
            "No material has been chosen"
        } else if (!material.isItem) {
            "$material is not a valid material"
        } else if (RainbowQuartz.itemManager.containsItem(key)) {
           "An item with the id $key already exists"
        } else null
    }
}