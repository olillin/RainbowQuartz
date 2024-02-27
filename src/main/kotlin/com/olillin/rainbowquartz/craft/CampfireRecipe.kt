package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.CampfireRecipe as BukkitCampfireRecipe

@Suppress("UNUSED")
class CampfireRecipe(input: Ingredient) : CookingRecipe(input) {
    override val suffix: String
        get() = id

    init {
        cookTime = 600
    }

    override fun asBukkitRecipe(item: Item): BukkitCampfireRecipe {
        val recipe = BukkitCampfireRecipe(
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

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "group" to group,
            "amount" to amount,
            "input" to input,
            "exp" to exp,
            "cookTime" to cookTime
        )
    }

    companion object {
        const val id = "campfire"
        val material = Material.CAMPFIRE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): CampfireRecipe {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            val input: Ingredient = section.getObject("input", Ingredient::class.java)
                ?: throw IllegalArgumentException("Invalid value for property 'input'")
            val recipe = CampfireRecipe(input)

            recipe.cookTime = section.getInt("cookTime", 600)
            recipe.exp = section.getDouble("exp", 0.0).toFloat()

            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            recipe.amount = section.getInt("amount", 1)

            return recipe
        }
    }
}