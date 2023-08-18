package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem

import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.plugin.gui.InventoryClickLinkEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.KeyMenu
import dev.hoodieboi.rainbowquartz.plugin.gui.LinkItem
import dev.hoodieboi.rainbowquartz.plugin.gui.enchanted
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler

@KeyMenu
class EditItemGeneralMenu(viewer: HumanEntity, builder: ItemBuilder, override val previousMenu: Menu?) :
    EditItemMenu(viewer, builder) {

    init {
        val itemName = (builder.build().item.displayName() as? TranslatableComponent)?.args()?.get(0)
            ?: Component.text("Name Unavailable").color(NamedTextColor.DARK_GRAY)

        // Items
        inventory.setItem(GENERAL_SLOT, inventory.getItem(GENERAL_SLOT)?.enchanted())

        inventory.setItem(3, LinkItem.makeLink(
                "rename",
                Material.NAME_TAG,
                Component.text("Rename").color(NamedTextColor.LIGHT_PURPLE),
                listOf(
                    Component.text("Current name"),
                    Component.text(" ").color(NamedTextColor.WHITE).append(itemName)
                )
            )
        )
        inventory.setItem(5, EMPTY_PANEL)
        inventory.setItem(6, EMPTY_PANEL)
        inventory.setItem(7, EMPTY_PANEL)
        inventory.setItem(8, EMPTY_PANEL)
        inventory.setItem(12, EMPTY_PANEL)
        inventory.setItem(13, EMPTY_PANEL)
        inventory.setItem(14, EMPTY_PANEL)
        inventory.setItem(15, EMPTY_PANEL)
        inventory.setItem(16, EMPTY_PANEL)
        inventory.setItem(17, EMPTY_PANEL)
        inventory.setItem(21, EMPTY_PANEL)
        inventory.setItem(22, EMPTY_PANEL)
        inventory.setItem(23, EMPTY_PANEL)
        inventory.setItem(24, EMPTY_PANEL)
        inventory.setItem(25, EMPTY_PANEL)
        inventory.setItem(26, EMPTY_PANEL)
    }

    @EventHandler
    fun onLink(event: InventoryClickLinkEvent) {
        when (event.linkKey) {
            "rename" -> {
                viewer.playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON)
                RenameItemMenu(viewer, builder, this).open()
            }
        }
    }
}