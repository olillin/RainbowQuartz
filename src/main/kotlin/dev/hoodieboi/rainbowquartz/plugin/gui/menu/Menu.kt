package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.item.Item
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class Menu {
    abstract val viewer: HumanEntity
    abstract val inventory: Inventory

    open fun show() {
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        viewer.openInventory(inventory)
    }

    fun inView(event: InventoryEvent): Boolean {
        return event.inventory.type == inventory.type && event.viewers == inventory.viewers
    }

    companion object {
        val EMPTY_PANEL = ItemStack(Material.GRAY_STAINED_GLASS_PANE)

        init {
            val meta = EMPTY_PANEL.itemMeta
            meta.displayName(Component.empty())
            EMPTY_PANEL.itemMeta = meta
        }

        fun listedItem(item: Item): ItemStack {
            val result = ItemStack(item.item)
            val meta = result.itemMeta
            val lore = meta.lore() ?: ArrayList()
            lore.add(0, Component.text(item.key.toString()).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
            meta.lore(lore)
            result.itemMeta = meta
            return result
        }
    }
}

fun HumanEntity.playSound(sound: Sound) {
    playSound(net.kyori.adventure.sound.Sound.sound(sound, SoundCategory.MASTER, 1.0f, 1.0f))
}