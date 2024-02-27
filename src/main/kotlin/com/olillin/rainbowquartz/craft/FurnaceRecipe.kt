package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.FurnaceRecipe as BukkitFurnaceRecipe

@Suppress("UNUSED")
class FurnaceRecipe(input: Ingredient) : CookingRecipe(input) {
    override val suffix: String
        get() = id

    override fun asBukkitRecipe(item: Item): BukkitFurnaceRecipe {
        val recipe = BukkitFurnaceRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            },
            input,
            exp,
            cookTime
        )
        recipe.group = group
        return recipe
    }

    companion object {
        const val id = "furnace"
        val material = Material.FURNACE
        private const val DEFAULT_COOK_TIME: Int = 200

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): FurnaceRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val input: Ingredient = section.getObject("input", Ingredient::class.java)
                ?: throw IllegalArgumentException("Invalid value for property 'input'")
            val recipe = FurnaceRecipe(input)

            recipe.cookTime = section.getInt("cookTime", DEFAULT_COOK_TIME)
            recipe.exp = section.getDouble("exp").toFloat()

            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            recipe.amount = section.getInt("amount", 1)

            return recipe
        }
    }
}