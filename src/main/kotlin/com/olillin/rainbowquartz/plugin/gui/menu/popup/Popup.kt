package com.olillin.rainbowquartz.plugin.gui.menu.popup

import com.olillin.rainbowquartz.plugin.gui.menu.Menu

/** Used in combination with [Menu] to provide an easy way for the player to provide a value of type [T].  */
public interface Popup<in T> {
    public val callback: (T) -> Unit
}