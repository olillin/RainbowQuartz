package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.SmithingTransformRecipe as BukkitSmithingTransformRecipe

public class SmithingTransformRecipe(
    public var base: Ingredient,
    public var addition: Ingredient,
    public var template: Ingredient
) : Recipe<SmithingTransformRecipe, BukkitSmithingTransformRecipe>() {

    override val recipeId: String
        get() = ID

    override fun asBukkitRecipe(item: Item): BukkitSmithingTransformRecipe {
        return BukkitSmithingTransformRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            },
            template.toRecipeChoice(),
            base.toRecipeChoice(),
            addition.toRecipeChoice()
        )
    }

    public fun setBase(base: Ingredient): SmithingTransformRecipe {
        this.base = base
        return this
    }

    public fun setAddition(addition: Ingredient): SmithingTransformRecipe {
        this.addition = addition
        return this
    }

    public fun setTemplate(template: Ingredient): SmithingTransformRecipe {
        this.template = template
        return this
    }

    override fun toString(): String =
        "${this::class.simpleName}(amount=$amount${", group=$group".takeIf { group.isNotEmpty() }}, base=$base, addition=$addition, template=$template)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmithingTransformRecipe

        if (base != other.base) return false
        if (amount != other.amount) return false
        if (addition != other.addition) return false
        if (template != other.template) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + base.hashCode()
        result = 31 * result + addition.hashCode()
        result = 31 * result + template.hashCode()
        return result
    }

    public override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "group" to group,
            "base" to base,
            "addition" to addition,
            "template" to template
        )
    }

    public companion object {
        internal const val ID = "smithing_transform"
        internal val ICON = Material.SMITHING_TABLE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        public fun deserialize(args: Map<String, Any>): SmithingTransformRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val base: Ingredient = section.getObject("base", Ingredient::class.java)
                ?: throw IllegalArgumentException("Invalid or missing property 'base'")
            val addition: Ingredient = section.getObject("addition", Ingredient::class.java)
                ?: throw IllegalArgumentException("Invalid or missing property 'addition'")
            val template: Ingredient = section.getObject("template", Ingredient::class.java)
                ?: throw IllegalArgumentException("Invalid or missing property 'template'")
            val recipe = SmithingTransformRecipe(base, addition, template)

            recipe.amount = section.getInt("amount", 1)
            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            return recipe
        }
    }
}