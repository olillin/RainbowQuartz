package com.olillin.rainbowquartz.craft

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

@Suppress("UNUSED")
abstract class CookingRecipe(protected var input: RecipeChoice) : Recipe() {
    protected var exp: Float = 0.0f
    protected var cookTime: Int = 200
    protected var group: String = ""
    protected var amount: Int = 1

    fun setInput(input: RecipeChoice): CookingRecipe {
        this.input = input
        return this
    }

    fun setInput(input: Material): CookingRecipe {
        return setInput(MaterialChoice(input))
    }

    fun setInput(input: ItemStack): CookingRecipe {
        return setInput(ExactChoice(input))
    }

    fun getInput(): RecipeChoice = input

    fun setExp(exp: Float): CookingRecipe {
        this.exp = exp
        return this
    }

    fun setCookTime(cookTime: Int): CookingRecipe {
        this.cookTime = cookTime
        return this
    }

    fun setGroup(group: String): CookingRecipe {
        this.group = group
        return this
    }

    fun getGroup(): String = group

    fun setAmount(amount: Int): CookingRecipe {
        this.amount = amount
        return this
    }

    fun getAmount(): Int = amount

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CookingRecipe

        if (group != other.group) return false
        if (input != other.input) return false
        if (exp != other.exp) return false
        if (cookTime != other.cookTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + cookTime
        return result
    }
}