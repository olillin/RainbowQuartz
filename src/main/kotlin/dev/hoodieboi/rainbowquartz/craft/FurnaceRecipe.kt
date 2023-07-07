package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class FurnaceRecipe(input: RecipeChoice) : CookingRecipe(input) {
    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.FurnaceRecipe {
        val recipe = org.bukkit.inventory.FurnaceRecipe(
            NamespacedKey.fromString(item.key.toString() + ".furnace")!!,
            item.result,
            input,
            exp,
            cookTime
        )
        recipe.group = group
        return recipe
    }
}