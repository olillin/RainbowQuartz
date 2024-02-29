package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.Recipe
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.popup.StringPopup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent

public abstract class GroupRecipePopup<T: Recipe<*, *>> : RecipePopup<T>() {
    protected var group: String = ""
    protected open val groupSlot: Int = 4

    @EventHandler
    public fun onOpenGroupRecipePopup(event: InventoryOpenEvent) {
        renderGroup()
    }

    @EventHandler
    public fun onLinkGroupRecipePopup(event: InventoryClickLinkEvent) {
        if (event.linkKey == "group") {
            StringPopup(viewer, group, this) {
                group = it
                renderGroup()
            }.open()
        }
    }

    protected open fun renderGroup() {
        inventory.setItem(
            groupSlot, LinkItem.makeLink(
                "group",
                Material.CHEST,
                Component.text("Set group").color(NamedTextColor.YELLOW),
                listOf(
                    Component.text("Current: ").append(
                        Component.text(group).color(NamedTextColor.GREEN)
                    )
                )
            )
        )
    }
}