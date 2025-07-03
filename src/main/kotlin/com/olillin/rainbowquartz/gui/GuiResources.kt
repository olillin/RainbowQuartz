package com.olillin.rainbowquartz.gui

import com.olillin.rainbowquartz.craft.GuiRecipe
import com.olillin.rainbowquartz.event.GuiEventHandler
import com.olillin.rainbowquartz.event.GuiEventPredicate
import com.olillin.rainbowquartz.gui.menu.Menu
import com.olillin.rainbowquartz.gui.menu.popup.Popup
import org.bukkit.entity.HumanEntity
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction

/**
 * Utility class for storing and retrieving classes for use in GUI.
 */
public class GuiResources internal constructor() {
    private val recipes: MutableList<Class<GuiRecipe<*, *>>> = mutableListOf()
    private val eventHandlers: MutableList<Class<GuiEventHandler<*, *>>> = mutableListOf()
    private val eventPredicates: MutableList<Class<GuiEventPredicate<*, *>>> = mutableListOf()

    public fun registerRecipe(clazz: Class<GuiRecipe<*, *>>) {
        val popup: Method = clazz.tryGetMethod(
            "popup", arrayOf(
                HumanEntity::class.java, clazz, GuiRecipe::class.java, Menu::class.java, KFunction::class.java
            ), Popup::class.java
        ) ?: throw IllegalArgumentException("Class does not implement 'popup' method.")

        // Check if static
        if (!Modifier.isStatic(popup.modifiers)) {
            throw IllegalArgumentException("Method 'popup' in class is not static")
        }
        // Check generic types
        popup.genericReturnType != Popup::class.java
        recipes.add(clazz)
    }

    public fun unregisterRecipe(clazz: Class<GuiRecipe<*, *>>) {
        recipes.remove(clazz)
    }

    public fun registerEventHandler(clazz: Class<GuiEventHandler<*, *>>) {
        eventHandlers.add(clazz)
    }

    public fun unregisterEventHandler(clazz: Class<GuiEventHandler<*, *>>) {
        eventHandlers.remove(clazz)
    }

    public fun registerEventPredicate(clazz: Class<GuiEventPredicate<*, *>>) {
        eventPredicates.add(clazz)
    }

    public fun unregisterEventPredicate(clazz: Class<GuiEventPredicate<*, *>>) {
        eventPredicates.remove(clazz)
    }
}