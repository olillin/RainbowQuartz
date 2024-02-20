package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.InsertMaterialMenu
import com.olillin.rainbowquartz.plugin.gui.menu.Menu
import com.olillin.rainbowquartz.plugin.gui.menu.fill
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory

class MaterialPopup(override val viewer: HumanEntity, private val placeholder: Material?, override val previousMenu: Menu?, override val callback: (Material) -> Unit) : InsertMaterialMenu(), Popup<Material> {
    override val inventory: Inventory = Bukkit.createInventory(viewer, 9, Component.text("Choose a material"))
    override val insertSlots = listOf(0)
    private var material: Material? = placeholder

    init {
        inventory.fill(EMPTY_PANEL)
        inventory.setItem(4, LinkItem.CANCEL)
        insertMaterial(0, material)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "submit" -> {
                if (material == null) {
                    viewer.sendMessage(
                        Component.text("Could not submit, no material has been chosen").color(RED)
                    )
                    return
                }
                callback(material!!)
                if (activeViewers().contains(viewer)) {
                    back()
                }
            }
            "invalid" -> {
                viewer.sendMessage(
                    Component.text("Could not submit, no material has been chosen").color(RED)
                )
            }
            "cancel" -> {
                back()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onChange(event: InventoryEvent) {
        material = inventory.getItem(0)?.type
        inventory.setItem(
            8,
            if (material == null) {
                LinkItem.makeLink(
                    "invalid",
                    Material.BARRIER,
                    Component.text("Invalid input").color(RED),
                    listOf(
                            Component.text("No material has been chosen")
                    )
                )
            } else {
                LinkItem.SUBMIT
            }
        )
    }
}