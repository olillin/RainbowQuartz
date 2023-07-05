package dev.hoodieboi.rainbowquartz

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class RainbowQuartz : JavaPlugin() {

    override fun onEnable() {
        // Create result
        var result = ItemStack(Material.IRON_SWORD)
        var resultMeta = result.itemMeta
        resultMeta.displayName(Component.text("Quartz sword"))
        resultMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, false)
        result.itemMeta = resultMeta
        // Create recipe
        var recipe = ShapedRecipe(NamespacedKey(this, "myrecipe"), result)
        recipe.shape("Q  ", "Q  ", "S  ")
        recipe.setIngredient('Q', Material.QUARTZ)
        recipe.setIngredient('S', Material.STICK)

        Bukkit.addRecipe(recipe)
    }
}