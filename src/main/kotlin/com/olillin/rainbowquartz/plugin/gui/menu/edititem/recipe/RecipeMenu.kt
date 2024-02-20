package com.olillin.rainbowquartz.plugin.gui.menu.edititem.recipe

import com.olillin.rainbowquartz.craft.Recipe

interface RecipeMenu<T : Recipe> {
    fun createRecipe(): T
}