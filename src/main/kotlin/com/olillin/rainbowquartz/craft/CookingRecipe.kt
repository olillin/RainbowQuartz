package com.olillin.rainbowquartz.craft

@Suppress("UNUSED")
abstract class CookingRecipe(var input: Ingredient) : Recipe() {
    open var exp: Float = 0.0f
    open var cookTime: Int = 200
    open var group: String = ""
    open var amount: Int = 1

    fun setInput(input: Ingredient): CookingRecipe {
        this.input = input
        return this
    }

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

    fun setAmount(amount: Int): CookingRecipe {
        this.amount = amount
        return this
    }

    override fun toString(): String = "${this::class.simpleName}(amount=$amount${if (group.isNotEmpty()) ", group=$group" else ""}, exp=$exp, cookTime=$cookTime, input=$input)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CookingRecipe

        if (group != other.group) return false
        if (amount != other.amount) return false
        if (input != other.input) return false
        if (exp != other.exp) return false
        if (cookTime != other.cookTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + cookTime
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "amount" to amount,
            "group" to group,
            "exp" to exp.toDouble(),
            "cookTime" to cookTime,
            "input" to input,
        )
    }
}