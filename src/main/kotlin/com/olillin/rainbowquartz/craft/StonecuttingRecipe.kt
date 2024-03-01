package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.StonecuttingRecipe as BukkitStonecuttingRecipe

public class StonecuttingRecipe(public var input: Ingredient) : Recipe<StonecuttingRecipe, BukkitStonecuttingRecipe>() {
    override val recipeId: String
        get() = ID

    override fun asBukkitRecipe(item: Item): BukkitStonecuttingRecipe {
        return BukkitStonecuttingRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            },
            input.toRecipeChoice()
        )
    }

    public fun setInput(input: Ingredient): StonecuttingRecipe {
        this.input = input
        return this
    }

    override fun toString(): String =
        "${this::class.simpleName}(amount=$amount${", group=$group".takeIf { group.isNotEmpty() }}, input=$input)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StonecuttingRecipe

        if (amount != other.amount) return false
        if (group != other.group) return false
        if (input != other.input) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + input.hashCode()
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "group" to group,
            "input" to input
        )
    }

    public companion object {
        internal const val ID = "stonecutting"
        internal val ICON = Material.STONECUTTER

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        public fun deserialize(args: Map<String, Any>): StonecuttingRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val input: Ingredient = section.getObject("input", Ingredient::class.java)
                ?: throw IllegalArgumentException("Missing or invalid property 'input'")

            val recipe = StonecuttingRecipe(input)

            recipe.amount = section.getInt("amount", 1)
            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            return recipe
        }
    }
}