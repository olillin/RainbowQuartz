package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class Menu {
    protected abstract val viewer: HumanEntity
    protected abstract val inventory: Inventory
    abstract val previousMenu: Menu?

    init {
        if (previousMenu != null && previousMenu?.viewer != viewer) {
            throw IllegalArgumentException("Viewer of previous menu must be the same as this menu")
        }
    }

    /**
     * Make the viewer open the menu
     */
    open fun open() {
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        viewer.openInventory(inventory)
    }

    /**
     * Make the viewer open the previous menu in the menu tree.
     *
     * @param sound An optional sound to play
     */
    fun back(sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF) {
        sound?.let { viewer.playSound(it) }
        if (previousMenu != null) {
            previousMenu?.open()
        } else {
            viewer.closeInventory(InventoryCloseEvent.Reason.PLAYER)
        }
    }

    /**
     * Goes back to the last menu that matches the predicate. If none is found
     * and a [default] menu is specified that menu will be opened instead.
     *
     * @param default The default menu to go to if no matches were found
     * @param sound An optional sound to play
     * @param predicate The predicate to check against
     * @return The opened menu, or `null` if none was found and there was no [default]
     */
    fun backToPredicate(default: Menu? = null, sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, predicate: (Menu) -> Boolean): Menu? {
        sound?.let { viewer.playSound(it) }
        var prev: Menu? = previousMenu
        for (i in 0 until MAX_BACK_ITERATION_DEPTH) {
            if (prev == null) {
                default?.open()
                break
            }
            if (predicate(prev)) {
                prev.open()
                return prev
            }
            prev = prev.previousMenu
        }
        return null
    }

    /**
     * Force the viewer to close the menu.
     *
     * @param sound An optional sound to play
     */
    fun close(sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF) {
        sound?.let { viewer.playSound(it) }
        viewer.closeInventory(InventoryCloseEvent.Reason.PLAYER)
    }

    /**
     * Get all viewers currently viewing this inventory.
     */
    fun activeViewers(): List<HumanEntity> {
        return this.inventory.viewers.toList()
    }

    @EventHandler
    fun onCloseMenu(event: InventoryCloseEvent) {
        RainbowQuartz.guiEventDispatcher.unregisterMenu(this)
    }

    companion object {
        val EMPTY_PANEL = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
            val meta = itemMeta
            meta.displayName(Component.empty())
            itemMeta = meta
        }
        const val MAX_BACK_ITERATION_DEPTH: Int = 25
    }
}

fun HumanEntity.playSound(sound: Sound) {
    playSound(net.kyori.adventure.sound.Sound.sound(sound, SoundCategory.MASTER, 1.0f, 1.0f))
}

/**
 * Fill all empty slots of an inventory with an item
 */
fun Inventory.fill(stack: ItemStack) {
    for (slot in 0 until this.size) {
        if (this.getItem(slot) == null) {
            this.setItem(slot, stack)
        }
    }
}