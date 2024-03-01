@file:Suppress("MemberVisibilityCanBePrivate")

package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ShapedRecipe as BukkitShapedRecipe

public class ShapedRecipe(vararg pattern: String) : Recipe<ShapedRecipe, BukkitShapedRecipe>() {
    private lateinit var pattern: Array<String>
    private val ingredients: MutableMap<Char, Ingredient> = mutableMapOf()

    public override val recipeId: String
        get() = ID

    init {
        setPattern(pattern.toList())
    }

    override fun asBukkitRecipe(item: Item): BukkitShapedRecipe {
        val recipe = BukkitShapedRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            }
        )
        recipe.group = group

        val registeredIngredients = ingredients.keys
        for (c: Char in pattern.joinToString("").toCharArray().filter { c -> c != ' ' }) {
            if (!registeredIngredients.contains(c)) {
                throw IllegalStateException("Recipe does not contain a definition for ingredient '$c'")
            }
        }

        recipe.shape(*pattern)
        for (ingredient in ingredients) {
            recipe.setIngredient(ingredient.key, ingredient.value.toRecipeChoice())
        }

        return recipe
    }

    public fun getPattern(): List<String> = pattern.toList()

    public fun setPattern(pattern: List<String>): ShapedRecipe {
        val trimmedPattern = trimPattern(pattern.toList())
        if (trimmedPattern.isEmpty() || trimmedPattern.size > 3) {
            throw IllegalArgumentException("Expected pattern height to be 1, 2 or 3, but got ${trimmedPattern.size}")
        }
        val maxWidth = trimmedPattern.maxOf { it.length }
        if (maxWidth == 0 || maxWidth > 3) {
            throw IllegalArgumentException("Expected pattern width to be 1, 2 or 3, but got $maxWidth")
        }
        this.pattern = trimmedPattern.toTypedArray()
        return this
    }

    public fun getIngredient(key: Char): Ingredient? = ingredients[key]?.clone()

    public fun setIngredient(key: Char, ingredient: Ingredient): ShapedRecipe {
        ingredients[key] = ingredient.clone()
        return this
    }

    override fun toString(): String {
        val patternString = pattern.joinToString(", ") { "\"$it\"" }
        return "${this::class.simpleName}(amount=$amount${", group=$group".takeIf { group.isNotEmpty() }}, pattern=[$patternString])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapedRecipe

        if (amount != other.amount) return false
        if (group != other.group) return false
        if (!(pattern contentEquals other.pattern)) return false
        if (ingredients != other.ingredients) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + pattern.contentHashCode()
        result = 31 * result + ingredients.hashCode()
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "group" to group,
            "pattern" to padPattern(pattern.asIterable()).toList(),
            "ingredients" to ingredients.map { (key, value) ->
                key.toString() to value
            }.toMap()
        )
    }

    public companion object {
        internal const val ID = "shaped"
        internal val ICON = Material.CRAFTING_TABLE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        public fun deserialize(args: Map<String, Any>): ShapedRecipe {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section.set(key, value)
            }

            val shape: Array<String> = section.getStringList("pattern").toTypedArray()
            if (shape.isEmpty()) {
                throw IllegalArgumentException("Missing or invalid property 'pattern'")
            }
            val recipe = ShapedRecipe(*shape)

            val ingredients =
                section.get("ingredients") ?: throw IllegalArgumentException("Missing property 'ingredients'")
            if (ingredients !is Map<*, *>) {
                throw IllegalArgumentException("Invalid property 'ingredients'")
            }
            for (key in ingredients.keys) {
                if (key !is String) throw IllegalArgumentException("Invalid key '$key', must be of type String")
                if (key.length != 1) throw IllegalArgumentException("Invalid key '$key', must be of length 1")
                val ingredient: Ingredient = ingredients[key] as? Ingredient
                    ?: throw IllegalArgumentException("Invalid ingredient class for key '$key', expected Ingredient")
                recipe.setIngredient(key[0], ingredient)
            }

            recipe.group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")

            recipe.amount = section.getInt("amount", 1)

            return recipe
        }

        /**
         * Trim pattern to minimum size.
         *
         * @see padPattern
         */
        public fun trimPattern(pattern: Iterable<String>): List<String> {
            // Trim vertically
            val trimmedVertically = pattern.toMutableList().apply {
                while (first().isBlank()) {
                    removeFirst()
                }
                while (last().isBlank()) {
                    removeLast()
                }
            }
            // Trim horizontally
            val width = trimmedVertically.maxOf { it.trim().length }
            return pattern.map {
                it.trimStart().padStart(width)
                    .trimEnd().padEnd(width)
            }.toList()
        }

        /**
         * Pad pattern to fill grid.
         *
         * @see trimPattern
         */
        public fun padPattern(pattern: Iterable<String>, width: Int = 3, height: Int = width): List<String> {
            return pattern.map {
                // Pad horizontally
                it.padEnd(width, ' ')
            }.toMutableList().apply {
                // Pad vertically
                while (size < height) {
                    add(" ".repeat(width))
                }
            }.toList()
        }
    }
}