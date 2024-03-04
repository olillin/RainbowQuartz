package com.olillin.rainbowquartz.event

import com.olillin.rainbowquartz.gui.menu.Menu
import com.olillin.rainbowquartz.gui.menu.popup.Popup
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.HumanEntity
import org.bukkit.event.Event

public interface GuiEventPredicate<Self: GuiEventPredicate<Self, T>, T: Event>: EventPredicate<T>, ConfigurationSerializable {
    public fun popup(viewer: HumanEntity, placeholder: Self?, previousMenu: Menu?, callback: (Self) -> Unit): Popup<Self>
}
