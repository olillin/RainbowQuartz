package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

class FurnaceRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix: String = "furnace"
    constructor(input: Material) : this(MaterialChoice(input))

    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.FurnaceRecipe {
        val recipe = org.bukkit.inventory.FurnaceRecipe(
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