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
class FurnaceRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix: String
        get() = id

    constructor(input: Material) : this(MaterialChoice(input))

    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun asBukkitRecipe(item: Item): org.bukkit.inventory.FurnaceRecipe {
        val recipe = org.bukkit.inventory.FurnaceRecipe(
            key(item),
            item.getItem(),
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
            "input" to input.itemStack,
            "exp" to exp,
            "cookTime" to cookTime
        )
    }

    companion object {
        const val id = "furnace"
        val material = Material.FURNACE

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
            section.addDefaults(args)

            val input: ItemStack = section.getItemStack("input")
                ?: throw IllegalArgumentException("Invalid value for property 'input'")
            val recipe = FurnaceRecipe(input)

            val cookTime = section.getInt("cook_time")
            recipe.setCookTime(cookTime)

            val exp = section.getDouble("exp").toFloat()
            recipe.setExp(exp)

            val group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")
            recipe.setGroup(group)

            return recipe
        }
    }
}