package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class SmokingRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix = "smoking"
    init {
        cookTime = 100
    }

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.SmokingRecipe {
        val recipe = org.bukkit.inventory.SmokingRecipe(
            key(item),
            item.item,
            input,
            exp,
            cookTime
        )
        recipe.group = group
        return recipe
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "type" to "smoking",
            "group" to group,
            "input" to input,
            "exp" to exp,
            "cookTime" to cookTime
        )
    }
}