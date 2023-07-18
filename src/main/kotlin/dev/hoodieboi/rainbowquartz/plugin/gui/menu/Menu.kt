package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.item.Item
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

abstract class Menu {
    /**
     * An integer ID used to identify the menu
     */
    abstract val id: Int
    val groupId: Int = 5

    abstract fun showMenu(viewer: HumanEntity)
    abstract fun onClick(event: InventoryClickEvent)

    fun title(title: String) = prefix().append(Component.text(title))

    fun prefix() = Component.empty()
        .append(Component.empty().color(TextColor.color(groupId)))
        .append(Component.empty().color(TextColor.color(id)))

    fun inView(view: InventoryView): Boolean {
        val title = view.title()
        if (title !is TextComponent) {
            return false
        }

        return title.children().size >= 2
                && title.children()[0].color() == TextColor.color(groupId)
                && title.children()[1].color() == TextColor.color(id)
    }

    companion object {
        val EMPTY_ITEM = ItemStack(Material.GRAY_STAINED_GLASS_PANE)

        init {
            val meta = EMPTY_ITEM.itemMeta
            meta.displayName(Component.empty())
            EMPTY_ITEM.itemMeta = meta
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