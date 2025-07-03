package com.olillin.rainbowquartz.event

import com.olillin.rainbowquartz.gui.menu.Menu
import com.olillin.rainbowquartz.gui.menu.popup.Popup
import org.bukkit.configuration.serialization.ConfigurationSerializable
import com.olillin.rainbowquartz.gui.GuiResources
import org.bukkit.entity.HumanEntity
import org.bukkit.event.Event

/**
 * Represents an [EventHandler] that can be created and modified in a [Popup] and may be serialized.
 *
 * The object MUST implement a static method "popup" with the parameters `(HumanEntity, Self?, Menu?, (Self) -> Unit)`
 * and that returns a [Popup] for the class.
 *
 * In addition to implementing this interface, you must register the class with [GuiResources.registerEventHandler].
 */
public interface GuiEventHandler<Self: GuiEventHandler<Self, T>, T: Event>: EventHandler<T>, ConfigurationSerializable {
    public fun popup(viewer: HumanEntity, placeholder: Self?, previousMenu: Menu?, callback: (Self) -> Unit): Popup<Self>
}
