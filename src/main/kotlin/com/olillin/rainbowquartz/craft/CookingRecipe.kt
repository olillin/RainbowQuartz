package com.olillin.rainbowquartz.craft

import org.bukkit.inventory.Recipe as BukkitRecipe

@Suppress("UNCHECKED_CAST")
public abstract class CookingRecipe<Self : CookingRecipe<Self, T>, T : BukkitRecipe>(public var input: Ingredient) :
    Recipe<Self, T>() {
    public open var exp: Float = 0.0f
    public open var cookTime: Int = 200

    public fun setExp(exp: Float): Self {
        this.exp = exp
        return this as Self
    }

    public fun setCookTime(cookTime: Int): Self {
        this.cookTime = cookTime
        return this as Self
    }

    public fun setInput(input: Ingredient): Self {
        this.input = input
        return this as Self
    }

    override fun toString(): String =
        "${this::class.simpleName}(amount=$amount${", group=$group".takeIf { group.isNotEmpty() }}, exp=$exp, cookTime=$cookTime, input=$input)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Self

        if (amount != other.amount) return false
        if (group != other.group) return false
        if (exp != other.exp) return false
        if (cookTime != other.cookTime) return false
        if (input != other.input) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + cookTime.hashCode()
        result = 31 * result + input.hashCode()
        return result
    }

    override fun serialize(): Map<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "group" to group,
            "exp" to exp.toDouble(),
            "cookTime" to cookTime,
            "input" to input,
        )
    }
}