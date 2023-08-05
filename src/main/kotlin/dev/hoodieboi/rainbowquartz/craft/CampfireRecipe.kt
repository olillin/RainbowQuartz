package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

class CampfireRecipe(input: RecipeChoice) : CookingRecipe(input) {
    override val suffix: String
        get() = id

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

            val input: ItemStack = section.getItemStack("input")
                ?: throw IllegalArgumentException("Invalid value for property 'input'")
            val recipe = CampfireRecipe(input)

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

    init {
        cookTime = 600
    }

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun asBukkitRecipe(item: Item): org.bukkit.inventory.CampfireRecipe {
        return org.bukkit.inventory.CampfireRecipe(
            key(item),
            item.item,
            input,
            exp,
            cookTime
        )
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "group" to group,
            "input" to input.itemStack,
            "exp" to exp,
            "cookTime" to cookTime
        )
    }
}