package dev.hoodieboi.rainbowquartz.plugin.gui.menu.edititem.recipe

import dev.hoodieboi.rainbowquartz.craft.Recipe

interface RecipeMenu<T : Recipe> {
    fun createRecipe(): T
}