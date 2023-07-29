package dev.hoodieboi.rainbowquartz.craft

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

abstract class CookingRecipe(var input: RecipeChoice) : Recipe() {
    var group: String = ""
    var exp: Float = 0.0f
    var cookTime: Int = 200

    fun setInput(input: RecipeChoice): CookingRecipe {
        this.input = input
        return this
    }

    fun setInput(input: Material): CookingRecipe {
        return setInput(MaterialChoice(input))
    }

    fun setInput(input: ItemStack): CookingRecipe {
        return setInput(ExactChoice(input))
    }

    fun setExp(exp: Float): CookingRecipe {
        this.exp = exp
        return this
    }

    fun setCookTime(cookTime: Int): CookingRecipe {
        this.cookTime = cookTime
        return this
    }

    fun setGroup(group: String): CookingRecipe {
        this.group = group
        return this
    }
}