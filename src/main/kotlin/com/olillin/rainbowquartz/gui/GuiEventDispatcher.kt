package com.olillin.rainbowquartz.gui

import com.destroystokyo.paper.event.block.AnvilDamagedEvent
import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import com.olillin.rainbowquartz.gui.menu.Menu
import org.bukkit.event.*
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException

@Suppress("MemberVisibilityCanBePrivate")
public class GuiEventDispatcher(private val plugin: Plugin) : Listener {
    private val menus = mutableSetOf<Menu>()

    /** Start listening for events. */
    internal fun start() {
        for (eventType in SUPPORTED_EVENTS) {
            plugin.server.pluginManager.registerEvent(
                eventType,
                this,
                EventPriority.HIGH,
                MethodHandleEventExecutor(
                    eventType,
                    GuiEventDispatcher::class.java.getMethod("onEvent", InventoryEvent::class.java)
                ),
                plugin
            )
        }
    }

    /**
     * Register a [menu] to handle events for.
     * @return `true` if the menu has been registered, `false` if the element is already registered.
     */
    public fun registerMenu(menu: Menu): Boolean {
        return menus.add(menu)
    }

    /**
     * Unregister a [menu] to no longer handle events for.
     * @return `true` if the menu has been unregistered; `false` if it was not registered.
     */
    public fun unregisterMenu(menu: Menu): Boolean {
        return menus.remove(menu)
    }

    /**
     * Called when any inventory event happens.
     */
    @EventHandler
    public fun onEvent(event: InventoryEvent) {
        // Link events
        if (event is InventoryClickEvent && !event.isCancelled
            && event !is InventoryClickLinkEvent
            && InventoryClickLinkEvent.isLinkClick(event)
        ) {
            try {
                onEvent(InventoryClickLinkEvent.fromClickEvent(event))
            } catch (_: IllegalArgumentException) {
                // Clicked item is not a link item
            }
        }

        val menu: Menu = getMenu(event.view.topInventory) ?: return
        invokeEvent(event, menu)
        if (event is InventoryCloseEvent) {
            unregisterMenu(menu)
        }
    }

    /** Get the menu associated with an [inventory]. */
    public fun getMenu(inventory: Inventory): Menu? = menus.firstOrNull { it.activeViewers() == inventory.viewers }

    /** Invoke an [event] handlers on a [menu]. */
    public fun invokeEvent(event: InventoryEvent, menu: Menu) {
        if (event is Cancellable && event.isCancelled) return
        // Filter and sort
        val methods = menu::class.java.methods.filter { method ->
            method.isAnnotationPresent(EventHandler::class.java)
                    && method.parameters.size == 1
                    && method.parameters[0].type.isAssignableFrom(event.javaClass)
        }.filterNot { method ->
            event is Cancellable && event.isCancelled
                    && method.getAnnotation(EventHandler::class.java).ignoreCancelled
        }.sortedByDescending { method ->
            val annotation = method.getAnnotation(EventHandler::class.java)!!
            annotation.priority.slot
        }
        // Invoke handlers
        for (method in methods) {
            try {
                method.invoke(menu, event)
            } catch (e: InvocationTargetException) {
                plugin.logger.severe("Error occurred while passing event of type ${event.javaClass.name} to event handler ${method}: ${e.targetException.message}")
            }
            if (event is Cancellable && event.isCancelled) break
        }
    }

    public companion object {
        public val SUPPORTED_EVENTS: Set<Class<out Event>> = setOf(
            AnvilDamagedEvent::class.java,
            CraftItemEvent::class.java,
            EnchantItemEvent::class.java,
            InventoryClickEvent::class.java,
            InventoryCloseEvent::class.java,
            InventoryCreativeEvent::class.java,
            InventoryDragEvent::class.java,
            InventoryOpenEvent::class.java,
            PrepareAnvilEvent::class.java,
            PrepareGrindstoneEvent::class.java,
            PrepareItemCraftEvent::class.java,
            PrepareItemEnchantEvent::class.java,
            PrepareResultEvent::class.java,
            PrepareSmithingEvent::class.java,
            SmithItemEvent::class.java,
            TradeSelectEvent::class.java,
        )
    }
}