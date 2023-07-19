package dev.hoodieboi.rainbowquartz.plugin.gui

import com.destroystokyo.paper.event.block.AnvilDamagedEvent
import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.*
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

final class GuiEventDispatcher(val plugin: Plugin) : Listener {
    val supportedEvents = setOf(
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
     * Start handling events
     */
    fun start() {
        for (eventType in supportedEvents) {
            plugin.server.pluginManager.registerEvent(
                eventType.java,
                this,
                EventPriority.HIGH,
                MethodHandleEventExecutor(eventType.java, GuiEventDispatcher::class.java.getMethod("onEvent", InventoryEvent::class.java)),
                plugin
            )
        }
    }

    fun registerMenu(menu: Menu) {
        menus.add(menu)
    }

    @EventHandler
    fun onEvent(event: InventoryEvent) {
        val iterator = menus.iterator()
        while (iterator.hasNext()) {
            val menu = iterator.next()
            if (menu.inView(event)) {
                invokeEvent(event, menu)
                if (event is InventoryCloseEvent) {
                    iterator.remove()
                }
            }
        }
    }

    fun invokeEvent(event: InventoryEvent, menu: Menu) {
        // Filter
        val menuMethods = menu.javaClass.methods.filter{method ->
            method.isAnnotationPresent(EventHandler::class.java)
                && method.parameters.map{p -> p.type} == listOf(event.javaClass)
        }
        // Order by priority
        val prioritizedMethods = HashMap<EventPriority, MutableList<Method>>()
        for (method in menuMethods) {
            val annotation = method.getAnnotation(EventHandler::class.java)!!
            val priority = annotation.priority
            if (prioritizedMethods.containsKey(priority)) {
                prioritizedMethods[priority]!!.add(method)
            } else {
                prioritizedMethods[priority] = mutableListOf(method)
            }
        }
        val priorities = prioritizedMethods.keys.toList().sortedByDescending{priority -> priority.slot}
        // Invoke handlers
        for (priority in priorities) {
            prioritizedMethods[priority]!!.forEach{method ->
                try {
                    method.invoke(menu, event)
                } catch(e: InvocationTargetException) {
                    plugin.logger.severe("Error occurred while processing event ${event.javaClass.name}: ${e.targetException.message}")
                }
            }
        }
    }
}