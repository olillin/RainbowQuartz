package com.olillin.rainbowquartz.plugin.gui.menu.popup.recipe

import com.olillin.rainbowquartz.craft.CookingRecipe
import com.olillin.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import com.olillin.rainbowquartz.plugin.gui.LinkItem
import com.olillin.rainbowquartz.plugin.gui.menu.popup.FloatPopup
import com.olillin.rainbowquartz.plugin.gui.menu.popup.IntPopup
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryOpenEvent

abstract class CookingRecipePopup<T: CookingRecipe> : GroupRecipePopup<T>() {
    protected open var exp: Float = 0f
    protected open var cookTime: Int = 200
    override val insertSlots: List<Int>
        get() = listOf(INPUT_SLOT)

    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("UNUSED_PARAMETER")
    fun onOpenCookingRecipe(event: InventoryOpenEvent) {
        inventory.setItem(1, EMPTY_PANEL)
        inventory.setItem(2, EMPTY_PANEL)
        inventory.setItem(3, EMPTY_PANEL)

        inventory.setItem(12, EMPTY_PANEL)

        inventory.setItem(19, EMPTY_PANEL)
        inventory.setItem(20, EMPTY_PANEL)
        inventory.setItem(21, EMPTY_PANEL)

        renderExp()
        renderCookTime()
    }

    @EventHandler
    fun onLinkCookingRecipe(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "exp" -> {
                FloatPopup(viewer, exp, this) {
                    exp = it
                    renderExp()
                }.open()
            }
            "cookTime" -> {
                IntPopup(viewer, cookTime, this) {
                    cookTime = it
                    renderCookTime()
                }.open()
            }
        }
    }

    private fun renderExp() {
        inventory.setItem(
            EXP_SLOT, LinkItem.makeLink(
                "exp",
                Material.EXPERIENCE_BOTTLE,
                Component.text("Set experience").color(NamedTextColor.YELLOW),
                listOf(
                    Component.text("Current: ").append(
                        Component.text(exp).color(NamedTextColor.GREEN)
                    )
                )
            )
        )
    }

    private fun renderCookTime() {
        inventory.setItem(COOK_TIME_SLOT, LinkItem.makeLink(
            "cookTime",
            Material.CLOCK,
            Component.text("Set cook time").color(NamedTextColor.YELLOW),
            listOf(
                Component.text("Current: ").append(
                    Component.text(cookTime).color(NamedTextColor.GREEN)
                )
            )
        ))
    }

    companion object {
        const val INPUT_LABEL_SLOT = 10
        const val INPUT_SLOT = 11
        const val COOK_TIME_SLOT = 13
        const val EXP_SLOT = 22
    }
}