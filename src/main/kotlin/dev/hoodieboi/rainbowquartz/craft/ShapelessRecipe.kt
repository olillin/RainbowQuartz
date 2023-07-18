package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class ShapelessRecipe : Recipe() {
    override val suffix = "shapeless"
    val ingredients: MutableList<RecipeChoice> = ArrayList()
    var group: String = ""
    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.ShapelessRecipe {
        val recipe = org.bukkit.inventory.ShapelessRecipe(
            key(item),
            item.item
        )
        recipe.group = group

        for (ingredient in ingredients) {
            recipe.addIngredient(ingredient)
        }
        return recipe
    }

    fun addIngredient(ingredient: RecipeChoice, amount: Int): ShapelessRecipe {
        ingredients.addAll(List(amount) {ingredient})
        return this
    }

    fun addIngredient(ingredient: Material, amount: Int): ShapelessRecipe {
        return addIngredient(MaterialChoice(ingredient), amount)
    }

    fun addIngredient(ingredient: ItemStack, amount: Int): ShapelessRecipe {
        return addIngredient(ExactChoice(ingredient), amount)
    }

    fun addIngredient(ingredient: RecipeChoice): ShapelessRecipe {
        ingredients.add(ingredient)
        return this
    }

    fun addIngredient(ingredient: Material): ShapelessRecipe {
        return addIngredient(MaterialChoice(ingredient))
    }

    fun addIngredient(ingredient: ItemStack): ShapelessRecipe {
        return addIngredient(ExactChoice(ingredient))
    }

    fun removeIngredient(ingredient: RecipeChoice, amount: Int): ShapelessRecipe {
        repeat(amount) {
            removeIngredient(ingredient)
        }
        return this
    }

    fun removeIngredient(ingredient: Material, amount: Int): ShapelessRecipe {
        return removeIngredient(MaterialChoice(ingredient), amount)
    }

    fun removeIngredient(ingredient: ItemStack, amount: Int): ShapelessRecipe {
        return removeIngredient(ExactChoice(ingredient), amount)
    }

    fun removeIngredient(ingredient: RecipeChoice): ShapelessRecipe {
        ingredients.remove(ingredient)
        return this
    }

    fun removeIngredient(ingredient: Material): ShapelessRecipe {
        return removeIngredient(MaterialChoice(ingredient))
    }

    fun removeIngredient(ingredient: ItemStack): ShapelessRecipe {
        return removeIngredient(ExactChoice(ingredient))
    }

    fun setGroup(group: String): ShapelessRecipe {
        this.group = group
        return this
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "type" to suffix,
            "group" to group,
            "ingredients" to ingredients
        )
    }
}