package com.olillin.rainbowquartz.plugin.gui.menu

import com.olillin.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

public abstract class Menu {
    protected abstract val viewer: HumanEntity
    protected abstract val inventory: Inventory
    protected abstract val previousMenu: Menu?

    init {
        if (previousMenu != null && previousMenu?.viewer != viewer) {
            throw IllegalArgumentException("Viewer of previous menu must be the same as this menu")
        }
    }

    /**
     * Make the viewer open the menu
     */
    public open fun open() {
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        viewer.openInventory(inventory)
    }

    /**
     * Make the viewer open the previous menu in the menu tree.
     *
     * @param sound An optional sound to play
     */
    protected fun back(sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF) {
        sound?.let { viewer.playSound(it) }
        if (previousMenu != null) {
            previousMenu?.open()
        } else {
            viewer.closeInventory(InventoryCloseEvent.Reason.PLAYER)
        }
    }

    /**
     * Goes back to the last menu that matches the [predicate]. If none is found
     * and a [default] menu is specified that menu will be opened instead.
     *
     * @param sound An optional sound to play
     * @return The opened menu, or `null` if none was found and there was no [default]
     */
    protected fun backUntil(
        default: Menu? = null,
        sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF,
        predicate: (Menu) -> Boolean
    ): Menu? {
        sound?.let { viewer.playSound(it) }
        var prev: Menu? = previousMenu
        for (i in 0 until MAX_BACK_ITERATION_DEPTH) {
            if (prev == null) {
                break
            } else if (predicate(prev)) {
                prev.open()
                return prev
            }
            prev = prev.previousMenu
        }
        default?.open()
        return default
    }

    /**
     * Force the viewer to close the menu.
     *
     * @param sound An optional sound to play
     */
    public fun close(sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF) {
        sound?.let { viewer.playSound(it) }
        viewer.closeInventory(InventoryCloseEvent.Reason.PLAYER)
    }

    /**
     * Get all viewers currently viewing this inventory.
     */
    public fun activeViewers(): List<HumanEntity> {
        return this.inventory.viewers.toList()
    }

    @Suppress("UNUSED_PARAMETER")
    @EventHandler
    public fun onCloseMenu(event: InventoryCloseEvent) {
        RainbowQuartz.guiEventDispatcher.unregisterMenu(this)
    }

    public companion object {
        public val EMPTY_PANEL: ItemStack = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
            val meta = itemMeta
            meta.displayName(Component.empty())
            itemMeta = meta
        }
        private const val MAX_BACK_ITERATION_DEPTH: Int = 50
    }
}

public fun HumanEntity.playSound(sound: Sound) {
    playSound(net.kyori.adventure.sound.Sound.sound(sound, SoundCategory.MASTER, 1.0f, 1.0f))
}

/** Fill all empty slots of an inventory with an item */
public fun Inventory.fill(stack: ItemStack) {
    for (slot in 0 until size) {
        if (getItem(slot)?.isEmpty != false) {
            setItem(slot, stack)
        }
    }
}