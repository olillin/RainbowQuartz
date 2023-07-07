package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class ShapedRecipe(vararg val shape: String) : Recipe {
    private val ingredients: MutableMap<Char, RecipeChoice> = HashMap()
    private var group: String = ""

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.ShapedRecipe {
        val recipe = org.bukkit.inventory.ShapedRecipe(
            NamespacedKey.fromString(item.key.toString() + ".shaped")!!,
            item.result
        )
        recipe.group = group

        val registeredIngredients = ingredients.keys
        for (c: Char in shape.joinToString("").toCharArray().filter{ c -> c != ' ' }) {
            if (!registeredIngredients.contains(c)) {
                throw IllegalStateException("Recipe does not contain a definition for ingredient '$c'")
            }
        }

        recipe.shape(*shape)
        for (ingredient in ingredients) {
            recipe.setIngredient(ingredient.key, ingredient.value)
        }

        return recipe
    }

    fun getIngredient(key: Char): RecipeChoice? {
        return ingredients[key]
    }

    fun setIngredient(key: Char, ingredient: RecipeChoice): ShapedRecipe {
        ingredients[key] = ingredient
        return this
    }

    fun setIngredient(key: Char, ingredient: Material): ShapedRecipe {
        return setIngredient(key, MaterialChoice(ingredient))
    }

    fun setIngredient(key: Char, ingredient: ItemStack): ShapedRecipe {
        return setIngredient(key, ExactChoice(ingredient))
    }

    fun setGroup(group: String): ShapedRecipe {
        this.group = group
        return this
    }
}