package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ShapelessRecipe as BukkitShapelessRecipe

@Suppress("UNUSED")
class ShapelessRecipe : Recipe() {
    private val ingredients: MutableList<Ingredient> = mutableListOf()
    var group: String = ""
    var amount: Int = 1
    override val suffix: String
        get() = id

    override fun asBukkitRecipe(item: Item): BukkitShapelessRecipe {
        val recipe = BukkitShapelessRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            }
        )
        recipe.group = group

        for (ingredient in ingredients) {
            recipe.addIngredient(ingredient)
        }
        return recipe
    }

    fun addIngredient(ingredient: Ingredient, amount: Int = 1): ShapelessRecipe {
        ingredients.addAll(List(amount) {ingredient})
        return this
    }

    fun getIngredients(): List<Ingredient> {
        return ingredients.toList()
    }

    fun removeIngredient(ingredient: Ingredient, amount: Int = 1): ShapelessRecipe {
        repeat(amount) {
            removeIngredient(ingredient)
        }
        return this
    }

    fun setGroup(group: String): ShapelessRecipe {
        this.group = group
        return this
    }

    fun setAmount(amount: Int): ShapelessRecipe {
        this.amount = amount
        return this
    }

    override fun toString(): String {
        val ingredientsString = ingredients.joinToString(", ")
        return "${this::class.simpleName}(amount=$amount${if (group.isNotEmpty()) ", group=$group" else ""}, ingredients=[${ingredientsString}])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapelessRecipe

        if (group != other.group) return false
        if (amount != other.amount) return false
        if (ingredients != other.ingredients) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + ingredients.hashCode()
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "group" to group,
            "amount" to amount,
            "ingredients" to ingredients
        )
    }

    companion object {
        const val id = "shapeless"
        val material = Material.CRAFTING_TABLE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): ShapelessRecipe {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            val recipe = ShapelessRecipe()

            val ingredients = section.getList("ingredients") ?: throw IllegalArgumentException("Missing or invalid property 'ingredients'")
            for (ingredient in ingredients) {
                if (ingredient !is Ingredient) throw IllegalArgumentException("Invalid ingredient class, expected Ingredient")

                recipe.addIngredient(ingredient)
            }

            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            recipe.amount = section.getInt("amount", 1)

            return recipe
        }
    }
}