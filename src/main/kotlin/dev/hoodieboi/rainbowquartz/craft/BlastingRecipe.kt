package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class BlastingRecipe(input: RecipeChoice) : CookingRecipe(input) {
    init {
        cookTime = 100
    }

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.BlastingRecipe {
        val recipe = org.bukkit.inventory.BlastingRecipe(
            NamespacedKey.fromString(item.key.toString() + ".blasting")!!,
            item.result,
            input,
            exp,
            cookTime
        )
        recipe.group = group
        return recipe
    }
}