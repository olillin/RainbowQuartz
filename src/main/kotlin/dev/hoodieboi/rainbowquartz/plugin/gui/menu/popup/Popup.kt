package dev.hoodieboi.rainbowquartz.plugin.gui.menu.popup

import dev.hoodieboi.rainbowquartz.plugin.gui.menu.Menu

/** Used in combination with [Menu] to provide an easy way for the player to provide a value of type [T].  */
interface Popup<in T> {
    val callback: (T) -> Unit
}