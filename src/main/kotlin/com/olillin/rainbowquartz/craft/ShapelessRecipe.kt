@file:Suppress("MemberVisibilityCanBePrivate")

package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ShapelessRecipe as BukkitShapelessRecipe

public class ShapelessRecipe : Recipe<ShapelessRecipe, BukkitShapelessRecipe>() {
    private val ingredients: MutableList<Ingredient> = mutableListOf()
    override val recipeId: String
        get() = ID

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

    public fun getIngredients(): List<Ingredient> = ingredients.toList()

    public fun addIngredient(ingredient: Ingredient, amount: Int = 1): ShapelessRecipe {
        ingredients.addAll(List(amount) { ingredient })
        return this
    }

    public fun removeIngredient(ingredient: Ingredient, amount: Int = 1): ShapelessRecipe {
        repeat(amount) {
            removeIngredient(ingredient)
        }
        return this
    }

    public override fun toString(): String {
        val ingredientsString = ingredients.joinToString(", ")
        return "${this::class.simpleName}(amount=$amount${", group=$group".takeIf { group.isNotEmpty() }}, ingredients=[${ingredientsString}])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapelessRecipe

        if (amount != other.amount) return false
        if (group != other.group) return false
        if (!(ingredients.toTypedArray() contentEquals other.ingredients.toTypedArray())) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + ingredients.hashCode()
        return result
    }

    public override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "group" to group,
            "ingredients" to ingredients
        )
    }

    public companion object {
        internal const val ID = "shapeless"
        internal val ICON = Material.CRAFTING_TABLE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        public fun deserialize(args: Map<String, Any>): ShapelessRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val recipe = ShapelessRecipe()

            val ingredients = section.getList("ingredients")
                ?: throw IllegalArgumentException("Missing or invalid property 'ingredients'")
            for (ingredient in ingredients) {
                if (ingredient !is Ingredient) throw IllegalArgumentException("Invalid ingredient class, expected Ingredient")

                recipe.addIngredient(ingredient)
            }

            recipe.amount = section.getInt("amount", 1)
            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            return recipe
        }
    }
}