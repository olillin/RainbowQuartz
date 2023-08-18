package dev.hoodieboi.rainbowquartz.plugin.gui.menu.popup

interface Popup<in T> {
    val callback: (T) -> Unit
}