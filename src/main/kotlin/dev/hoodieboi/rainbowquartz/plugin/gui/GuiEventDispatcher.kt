package dev.hoodieboi.rainbowquartz.plugin.gui

import com.destroystokyo.paper.event.block.AnvilDamagedEvent
import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException

class GuiEventDispatcher(val plugin: Plugin) : Listener {
    private val supportedEvents = setOf(
        AnvilDamagedEvent::class,
        CraftItemEvent::class,
        EnchantItemEvent::class,
        InventoryClickEvent::class,
        InventoryCloseEvent::class,
        InventoryCreativeEvent::class,
        InventoryDragEvent::class,
        InventoryOpenEvent::class,
        PrepareAnvilEvent::class,
        PrepareGrindstoneEvent::class,
        PrepareItemCraftEvent::class,
        PrepareItemEnchantEvent::class,
        PrepareResultEvent::class,
        PrepareSmithingEvent::class,
        SmithItemEvent::class,
        TradeSelectEvent::class,
    )
    private val menus = mutableSetOf<Menu>()

    /**
     * Start handling events.
     */
    fun start() {
        for (eventType in supportedEvents) {
            plugin.server.pluginManager.registerEvent(
                eventType.java,
                this,
                EventPriority.HIGH,
                MethodHandleEventExecutor(
                    eventType.java,
                    GuiEventDispatcher::class.java.getMethod("onEvent", InventoryEvent::class.java)
                ),
                plugin
            )
        }
    }

    /**
     * Register a menu to handle events for.
     *
     * @param menu The menu
     * @return `true` if the menu has been registered, `false` if the element is already registered.
     */
    fun registerMenu(menu: Menu): Boolean {
        return menus.add(menu)
    }

    /**
     * Unregister a menu to no longer handle events for.
     *
     * @param menu The menu
     * @return `true` if the menu has been unregistered; `false` if it was not registered.
     */
    fun unregisterMenu(menu: Menu): Boolean {
        return menus.remove(menu)
    }

    /**
     * Called when any inventory event happens.
     */
    @EventHandler
    fun onEvent(event: InventoryEvent) {
        // Link events
        if (event is InventoryClickEvent && !event.isCancelled
            && event !is InventoryClickLinkEvent
            && InventoryClickLinkEvent.isLinkClick(event)) {
            try {
                onEvent(InventoryClickLinkEvent(event))
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

    /**
     * Get the menu associated with an inventory.
     *
     * @param inventory The inventory
     */
    fun getMenu(inventory: Inventory): Menu? = menus.firstOrNull { it.activeViewers() == inventory.viewers }

    /**
     * Invoke the event handlers on a menu.
     *
     * @param event The event to use
     * @param menu The menu to invoke the event handlers on
     */
    fun invokeEvent(event: InventoryEvent, menu: Menu) {
        if (event is Cancellable && event.isCancelled) return
        // Filter and sort
        val methods = menu::class.java.methods.filter { method ->
            method.isAnnotationPresent(EventHandler::class.java)
                && method.parameters.size == 1
                && method.parameters[0].type.isAssignableFrom(event.javaClass)
        }.filterNot {method ->
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
}