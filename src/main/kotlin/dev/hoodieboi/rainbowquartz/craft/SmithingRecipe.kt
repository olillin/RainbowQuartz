package dev.hoodieboi.rainbowquartz.craft

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

abstract class SmithingRecipe(var base: RecipeChoice, var addition: RecipeChoice) : Recipe {
    var group: String = ""

    fun setBase(base: RecipeChoice): SmithingRecipe {
        this.base = base
        return this
    }

    fun setBase(base: Material): SmithingRecipe {
        return setBase(MaterialChoice(base))
    }

    fun setBase(base: ItemStack): SmithingRecipe {
        return setBase(ExactChoice(base))
    }

    fun setAddition(addition: RecipeChoice): SmithingRecipe {
        this.addition = addition
        return this
    }

    fun setAddition(addition: Material): SmithingRecipe {
        return setAddition(MaterialChoice(addition))
    }

    fun setAddition(addition: ItemStack): SmithingRecipe {
        return setAddition(ExactChoice(addition))
    }

    fun setGroup(group: String): SmithingRecipe {
        this.group = group
        return this
    }
}