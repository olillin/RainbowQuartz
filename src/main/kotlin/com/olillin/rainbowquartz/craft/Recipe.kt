package com.olillin.rainbowquartz.craft

import com.olillin.rainbowquartz.item.Item
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.Recipe as BukkitRecipe

@Suppress("UNCHECKED_CAST")
public abstract class Recipe<Self : Recipe<Self, T>, T : BukkitRecipe> : ConfigurationSerializable {
    public var amount: Int = 1
    public var group: String = ""
    protected abstract val recipeId: String

    public abstract fun asBukkitRecipe(item: Item): T

    public fun setAmount(amount: Int): Self {
        this.amount = amount
        return this as Self
    }

    public fun setGroup(group: String): Self {
        this.group = group
        return this as Self
    }

    public fun key(item: Item): NamespacedKey = key(item.id)
    public fun key(itemKey: NamespacedKey): NamespacedKey =
        NamespacedKey.fromString("$itemKey/$recipeId-${hashCode().toUInt()}")!!
}