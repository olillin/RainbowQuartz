package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

class BlastingRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix
        get() = id
    init {
        cookTime = 100
    }

    companion object {
        const val id = "blasting"
        fun deserialize(args: Map<String, Any>): dev.hoodieboi.rainbowquartz.craft.BlastingRecipe {
            if (args["type"] != id) {
                throw InvalidTypeException(id)
            }

            val recipe = BlastingRecipe(args["input"] as RecipeChoice)

            val cookTime = args["cook_time"]
            if (cookTime !is Int) throw IllegalArgumentException("cook_time must be int")
            recipe.setCookTime(cookTime)

            val exp = args["exp"]
            if (exp !is Float) throw IllegalArgumentException("exp must be float")
            recipe.setExp(exp)

            val group = args["group"]
            if (group !is String) throw IllegalArgumentException("group must be string")
            recipe.setGroup(group)

            return recipe
        }
    }

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun toBukkitRecipe(item: Item): BlastingRecipe {
        val recipe = BlastingRecipe(
            key(item),
            item.item,
            input,
            exp,
            cookTime
        )
        recipe.group = group
        return recipe
    }
}