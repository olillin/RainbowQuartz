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
class ShapedRecipe(vararg val pattern: String) : Recipe() {
    private val ingredients: MutableMap<Char, RecipeChoice> = mutableMapOf()
    private var group: String = ""
    private var amount: Int = 1

    override val suffix: String
        get() = id

    init {
        if (pattern.size < 1 || pattern.size > 3) {
            throw IllegalArgumentException("Expected pattern to be of size 1, 2 or 3, but got size ${pattern.size}")
        }
        val width = pattern[0].length
        for (row in pattern) {
            if (row.length != width) {
                throw IllegalArgumentException("Inconsistent row lengths")
            }
            if (row.length < 1 || row.length > 3) {
                throw IllegalArgumentException("Expected row to be of length 1, 2 or 3, but got size ${row.length}")
            }
        }
    }

    override fun asBukkitRecipe(item: Item): org.bukkit.inventory.ShapedRecipe {
        val recipe = org.bukkit.inventory.ShapedRecipe(
            key(item),
            item.getItem().also {
                it.amount = amount
            }
        )
        recipe.group = group

        val registeredIngredients = ingredients.keys
        for (c: Char in pattern.joinToString("").toCharArray().filter{ c -> c != ' ' }) {
            if (!registeredIngredients.contains(c)) {
                throw IllegalStateException("Recipe does not contain a definition for ingredient '$c'")
            }
        }

        recipe.shape(*pattern)
        for (ingredient in ingredients) {
            recipe.setIngredient(ingredient.key, ingredient.value)
        }

        return recipe
    }

    fun getPattern(): List<String> {
        return pattern.toList()
    }

    fun getIngredient(key: Char): RecipeChoice? {
        return ingredients[key]?.clone()
    }

    fun setIngredient(key: Char, ingredient: RecipeChoice): ShapedRecipe {
        ingredients[key] = ingredient.clone()
        return this
    }

    fun setIngredient(key: Char, ingredient: Material): ShapedRecipe {
        return setIngredient(key, MaterialChoice(ingredient))
    }

    fun setIngredient(key: Char, ingredient: ItemStack): ShapedRecipe {
        return setIngredient(key, ExactChoice(ingredient))
    }

    fun setGroup(group: String): ShapedRecipe {
        this.group = group
        return this
    }

    fun getGroup(): String = group

    fun setAmount(amount: Int): ShapedRecipe {
        this.amount = amount
        return this
    }

    fun getAmount(): Int = amount

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "group" to group,
            "pattern" to pattern,
            "amount" to amount,
            "ingredients" to ingredients.map {
                it.key.toString() to it.value.itemStack
            }.toMap().toMutableMap()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapedRecipe

        if (group != other.group) return false
        if (!pattern.contentEquals(other.pattern)) return false
        if (ingredients != other.ingredients) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + pattern.contentHashCode()
        result = 31 * result + ingredients.hashCode()
        return result
    }

    companion object {
        const val id = "shaped"
        val material = Material.CRAFTING_TABLE

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): ShapedRecipe {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            if (!section.isList("pattern")) {
                throw IllegalArgumentException("Missing or invalid property 'pattern'")
            }
            val shape = section.getStringList("pattern").toTypedArray()
            val recipe = ShapedRecipe(*shape)

            val ingredients = section.get("ingredients") ?: throw IllegalArgumentException("Missing property 'ingredients'")
            if (ingredients !is Map<*, *>) {
                throw IllegalArgumentException("Invalid property 'ingredients'")
            }
            for (key in ingredients.keys) {
                if (key !is String) throw IllegalArgumentException("Invalid key '$key', must be of type String")
                if (key.length != 1) throw IllegalArgumentException("Invalid key '$key', must be of length 1")
                val item: ItemStack = ingredients[key] as? ItemStack ?: throw IllegalArgumentException("Invalid ingredient for key '$key'")
                recipe.setIngredient(key[0], item)
            }

            val group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")
            recipe.setGroup(group)

            val amount = section.getInt("amount", 1)
            recipe.setAmount(amount)

            return recipe
        }
    }
}