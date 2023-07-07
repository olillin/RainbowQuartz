package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

class ShapedRecipe(vararg val shape: String) : Recipe {
    private val ingredients: MutableMap<Char, RecipeChoice> = HashMap()

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.ShapedRecipe {
        val recipe = org.bukkit.inventory.ShapedRecipe(NamespacedKey.fromString(item.key.toString() + ".shaped")!!, item.result)

        val registeredIngredients = ingredients.keys
        for (c: Char in shape.joinToString("").toCharArray().filter{ c -> c != ' ' }) {
            if (!registeredIngredients.contains(c)) {
                throw IllegalStateException("Recipe does not contain definitions for ingredient '$c'")
            }
        }

        recipe.shape(*shape)
        for (ingredient in ingredients) {
            recipe.setIngredient(ingredient.key, ingredient.value)
        }

        return recipe
    }

    fun setIngredient(key: Char, ingredient: RecipeChoice): ShapedRecipe {
        ingredients[key] = ingredient
        return this
    }

    fun setIngredient(key: Char, ingredient: Material): ShapedRecipe {
        return setIngredient(key, RecipeChoice.MaterialChoice(ingredient))
    }

    fun setIngredient(key: Char, ingredient: ItemStack): ShapedRecipe {
        return setIngredient(key, RecipeChoice.ExactChoice(ingredient))
    }
}