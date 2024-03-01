package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.FurnaceRecipe as BukkitFurnaceRecipe

public class FurnaceRecipe(input: Ingredient) : CookingRecipe<FurnaceRecipe, BukkitFurnaceRecipe>(input) {
    override var cookTime: Int = DEFAULT_COOK_TIME
    override val recipeId: String
        get() = ID

    override fun asBukkitRecipe(item: Item): BukkitFurnaceRecipe {
        val recipe = BukkitFurnaceRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            },
            input.toRecipeChoice(),
            exp,
            cookTime
        )
        recipe.group = group
        return recipe
    }

    public companion object {
        private const val DEFAULT_COOK_TIME: Int = 200

        internal const val ID = "furnace"
        internal val ICON = Material.FURNACE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        public fun deserialize(args: Map<String, Any>): FurnaceRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val input: Ingredient = section.getObject("input", Ingredient::class.java)
                ?: throw IllegalArgumentException("Invalid value for property 'input'")

            val recipe = FurnaceRecipe(input)

            recipe.amount = section.getInt("amount", 1)
            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")
            recipe.exp = section.getDouble("exp").toFloat()
            recipe.cookTime = section.getInt("cookTime", DEFAULT_COOK_TIME)

            return recipe
        }
    }
}