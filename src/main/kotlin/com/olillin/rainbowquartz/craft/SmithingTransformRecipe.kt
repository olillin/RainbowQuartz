package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.SmithingTransformRecipe as BukkitSmithingTransformRecipe

@Suppress("UNUSED")
class SmithingTransformRecipe(var base: Ingredient, var addition: Ingredient, var template: Ingredient) : Recipe() {
    var amount: Int = 1
    override val suffix: String
        get() = id

    override fun asBukkitRecipe(item: Item): BukkitSmithingTransformRecipe {
        return BukkitSmithingTransformRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            },
            template,
            base,
            addition
        )
    }

    fun setBase(base: Ingredient): SmithingTransformRecipe {
        this.base = base
        return this
    }

    fun setAddition(addition: Ingredient): SmithingTransformRecipe {
        this.addition = addition
        return this
    }

    fun setTemplate(template: Ingredient): SmithingTransformRecipe {
        this.template = template
        return this
    }

    fun setAmount(amount: Int): SmithingTransformRecipe {
        this.amount = amount
        return this
    }

    override fun toString(): String = "${this::class.simpleName}(amount=$amount, base=$base, addition=$addition, template=$template)"

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
        result = 31 * result + base.hashCode()
        result = 31 * result + addition.hashCode()
        result = 31 * result + template.hashCode()
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "base" to base,
            "addition" to addition,
            "template" to template
        )
    }

    companion object {
        const val id = "smithing_transform"
        val material = Material.SMITHING_TABLE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): SmithingTransformRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val base: Ingredient = section.getObject("base", Ingredient::class.java) ?: throw IllegalArgumentException("Invalid or missing property 'base'")
            val addition: Ingredient = section.getObject("addition", Ingredient::class.java) ?: throw IllegalArgumentException("Invalid or missing property 'addition'")
            val template: Ingredient = section.getObject("template", Ingredient::class.java) ?: throw IllegalArgumentException("Invalid or missing property 'template'")
            val recipe = SmithingTransformRecipe(base, addition, template)

            recipe.amount = section.getInt("amount", 1)

            return recipe
        }
    }
}