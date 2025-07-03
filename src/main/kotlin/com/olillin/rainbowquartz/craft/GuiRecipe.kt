package com.olillin.rainbowquartz.craft

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.Recipe as BukkitRecipe

public abstract class GuiRecipe<Self : GuiRecipe<Self, T>, T : BukkitRecipe> : Recipe<Self, T>(),
    ConfigurationSerializable