package dev.hoodieboi.rainbowquartz.craft

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

abstract class CookingRecipe(var input: RecipeChoice) : Recipe() {
    var exp: Float = 0.0f
    var cookTime: Int = 200
    var group: String = ""

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

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "type" to suffix,
            "group" to group,
            "input" to input,
            "exp" to exp,
            "cookTime" to cookTime
        )
    }
}