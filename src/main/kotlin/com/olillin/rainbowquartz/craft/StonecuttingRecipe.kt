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
class StonecuttingRecipe(var input: RecipeChoice) : Recipe() {
    var group: String = ""
    var amount: Int = 1
    override val suffix
        get() = id

    constructor(input: Material) : this(MaterialChoice(input))
    constructor(input: ItemStack) : this(ExactChoice(input))

    override fun asBukkitRecipe(item: Item): org.bukkit.inventory.StonecuttingRecipe {
        return org.bukkit.inventory.StonecuttingRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            },
            input
        )
    }

    fun setInput(input: RecipeChoice): StonecuttingRecipe {
        this.input = input
        return this
    }
    fun setInput(input: Material): StonecuttingRecipe {
        return setInput(MaterialChoice(input))
    }
    fun setInput(input: ItemStack): StonecuttingRecipe {
        return setInput(ExactChoice(input))
    }

    fun setGroup(group: String): StonecuttingRecipe {
        this.group = group
        return this
    }

    fun setAmount(amount: Int): StonecuttingRecipe {
        this.amount = amount
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StonecuttingRecipe

        if (group != other.group) return false
        if (amount != other.amount) return false
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
            "group" to group,
            "amount" to amount,
            "input" to input.itemStack
        )
    }

    companion object {
        const val id = "stonecutting"
        val material = Material.STONECUTTER

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): StonecuttingRecipe {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            val input: ItemStack = section.getItemStack("input") ?: throw IllegalArgumentException("Missing or invalid property 'input'")

            val recipe = StonecuttingRecipe(input)

            val group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")
            recipe.setGroup(group)

            return recipe
        }
    }
}