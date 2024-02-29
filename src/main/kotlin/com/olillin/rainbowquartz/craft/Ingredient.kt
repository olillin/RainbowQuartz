package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.meta.ItemMeta

public class Ingredient(public val material: Material, public val meta: ItemMeta? = null) : RecipeChoice,
    ConfigurationSerializable, Cloneable {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun getItemStack(): ItemStack {
        return ItemStack(material).apply {
            itemMeta = meta
            amount = 1
        }
    }

    override fun clone(): Ingredient {
        return Ingredient(material, meta?.clone())
    }

    override fun test(itemStack: ItemStack): Boolean {
        if (itemStack.type != material) return false
        return if (itemStack.itemMeta != ItemStack(itemStack.type).itemMeta) {
            // Has custom item meta
            itemStack.itemMeta == meta
        } else {
            meta == null
        }
    }

    override fun toString(): String {
        return "Ingredient(material=$material" + (if (meta != null) ", meta=$meta" else "") + ")"
    }

    override fun hashCode(): Int {
        var result = material.hashCode()
        result = 31 * result + (meta?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ingredient

        return material == other.material
                && meta == other.meta
    }

    override fun serialize(): Map<String, Any> {
        val serialized: MutableMap<String, Any> = mutableMapOf(
            "material" to material
        )
        if (meta != null) {
            serialized["meta"] = meta
        }
        return serialized
    }

    public companion object {
        @JvmStatic
        public fun fromItemStack(itemStack: ItemStack): Ingredient = Ingredient(itemStack.type, itemStack.itemMeta)

        @JvmStatic
        public fun fromItem(item: Item): Ingredient = fromItemStack(item.getItem())

        @JvmStatic
        public fun fromRecipeChoice(ingredient: RecipeChoice) {
            val material = when (ingredient) {
                is Ingredient -> ingredient.material
                is MaterialChoice -> ingredient.choices[0]
                is ExactChoice -> ingredient.choices[0].type
                else -> throw IllegalArgumentException("Unsupported ingredient type")
            }
            val meta = when (ingredient) {
                is Ingredient -> ingredient.meta
                is ExactChoice -> ingredient.choices[0].itemMeta
                is MaterialChoice -> null
                else -> throw IllegalArgumentException("Unsupported ingredient type")
            }
            Ingredient(material, meta)
        }

        @JvmStatic
        public fun deserialize(args: Map<String, Any>): Ingredient {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val material = section.getObject("material", Material::class.java)
                ?: throw IllegalArgumentException("Missing required parameter material")
            val meta = section.getObject("meta", ItemMeta::class.java)

            return Ingredient(material, meta)
        }
    }
}