package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class StonecuttingRecipe(var input: RecipeChoice) : Recipe {
    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.StonecuttingRecipe {
        return org.bukkit.inventory.StonecuttingRecipe(
            NamespacedKey.fromString(item.key.toString() + ".stonecutting")!!,
            item.result,
            input
        )
    }

    fun setInput(input: RecipeChoice): StonecuttingRecipe {
        this.input = input
        return this
    }
    fun setInput(input: Material): StonecuttingRecipe {
        return setInput(MaterialChoice(input))
    }
    fun setInput(input: ItemStack): StonecuttingRecipe {
        return setInput(ExactChoice(input))
    }
}