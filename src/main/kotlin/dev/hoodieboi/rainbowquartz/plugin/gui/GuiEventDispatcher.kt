package dev.hoodieboi.rainbowquartz.plugin.gui

import com.destroystokyo.paper.event.block.AnvilDamagedEvent
import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.*
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class GuiEventDispatcher(val plugin: Plugin) : Listener {
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
                MethodHandleEventExecutor(
                    eventType.java,
                    GuiEventDispatcher::class.java.getMethod("onEvent", InventoryEvent::class.java)
                ),
                plugin
            )
        }
    }

    fun registerMenu(menu: Menu) {
        menus.add(menu)
    }

    @EventHandler
    fun onEvent(event: InventoryEvent) {
        if (event is InventoryClickEvent && !event.isCancelled
            && event !is InventoryClickLinkEvent
            && setOf(
                ClickType.LEFT,
                ClickType.SHIFT_LEFT,
                ClickType.RIGHT,
                ClickType.SHIFT_RIGHT,
                ClickType.DOUBLE_CLICK
            ).contains(event.click)) {
            try {
                onEvent(InventoryClickLinkEvent(event))
            } catch (_: IllegalArgumentException) {}
        }
        val iterator = menus.iterator()
        while (iterator.hasNext()) {
            val menu = iterator.next()
            if (menu.inView(event)) {
                invokeEvent(event, menu)
                if (event is InventoryCloseEvent) {
                    iterator.remove()
                }
                break
            }
        }
    }

    fun invokeEvent(event: InventoryEvent, menu: Menu) {
        if (event is Cancellable && event.isCancelled) return
        // Filter
        val menuMethods = menu::class.java.methods.filter { method ->
            method.isAnnotationPresent(EventHandler::class.java)
                    && method.parameters.map { p -> p.type } == listOf(event.javaClass)
        }
        // Categorize by priority
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
        val methods = prioritizedMethods.toList()
            .sortedByDescending { (key, _) -> key.slot }
            .map { pair -> pair.second }
            .flatten()
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