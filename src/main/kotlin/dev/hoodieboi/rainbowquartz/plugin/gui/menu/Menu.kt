package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class Menu {
    protected abstract val viewer: HumanEntity
    protected abstract val inventory: Inventory
    protected abstract val previousMenu: Menu?

    init {
        if (viewer != previousMenu?.viewer) {
            throw IllegalArgumentException("Viewer of previous menu must be the same as this menu")
        }
    }

    open fun show() {
        RainbowQuartz.guiEventDispatcher.registerMenu(this)
        viewer.openInventory(inventory)
    }

    open fun back(sound: Sound? = Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF) {
        sound?.let { viewer.playSound(it) }
        if (previousMenu != null) {
            previousMenu?.show()
        } else {
            viewer.closeInventory(InventoryCloseEvent.Reason.PLAYER)
        }
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