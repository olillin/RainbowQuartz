package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import dev.hoodieboi.rainbowquartz.plugin.gui.KeyMenu
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
     * Make the viewer open the previous menu in the menu tree with the [KeyMenu] annotation.
     *
     * @param sound An optional sound to play
     */
    fun backToKey(sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF) {
        sound?.let { viewer.playSound(it) }
        var prev: Menu? = previousMenu
        while (prev != null) {
            if (prev::class.java.isAnnotationPresent(KeyMenu::class.java)) {
                prev.open()
                return
            }
            prev = prev.previousMenu
        }
        viewer.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
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

    companion object {
        val EMPTY_PANEL = ItemStack(Material.GRAY_STAINED_GLASS_PANE)

        init {
            val meta = EMPTY_PANEL.itemMeta
            meta.displayName(Component.empty())
            EMPTY_PANEL.itemMeta = meta
        }
    }

    @EventHandler
    fun onCloseMenu(event: InventoryCloseEvent) {
        RainbowQuartz.guiEventDispatcher.unregisterMenu(this)
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