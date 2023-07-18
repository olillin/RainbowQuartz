package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

class CampfireRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix = "campfire"
    init {
        cookTime = 600
    }

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.CampfireRecipe {
        return org.bukkit.inventory.CampfireRecipe(
            key(item),
            item.item,
            input,
            exp,
            cookTime
        )
    }
}