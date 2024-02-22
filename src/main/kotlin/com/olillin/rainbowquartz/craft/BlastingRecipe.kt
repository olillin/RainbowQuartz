package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

@Suppress("UNUSED")
class BlastingRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix: String
        get() = id

    init {
        cookTime = 100
    }

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun asBukkitRecipe(item: Item): org.bukkit.inventory.BlastingRecipe {
        val recipe = org.bukkit.inventory.BlastingRecipe(
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
            "input" to asItemStack(input),
            "exp" to exp,
            "cookTime" to cookTime
        )
    }

    companion object {
        const val id = "blasting"
        val material = Material.BLAST_FURNACE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): BlastingRecipe {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            val input: ItemStack = section.getItemStack("input")
                ?: throw IllegalArgumentException("Invalid value for property 'input'")
            val recipe = BlastingRecipe(input)

            recipe.cookTime = section.getInt("cookTime", 100)
            recipe.exp = section.getDouble("exp", 0.0).toFloat()

            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            recipe.amount = section.getInt("amount", 1)

            return recipe
        }
    }
}