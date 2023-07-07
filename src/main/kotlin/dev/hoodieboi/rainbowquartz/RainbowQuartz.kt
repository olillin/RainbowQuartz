package dev.hoodieboi.rainbowquartz

import dev.hoodieboi.rainbowquartz.craft.*
import dev.hoodieboi.rainbowquartz.event.EventDispatcher
import dev.hoodieboi.rainbowquartz.item.Item.ItemBuilder
import dev.hoodieboi.rainbowquartz.item.ItemManager
import dev.hoodieboi.rainbowquartz.plugin.command.GetItem
import dev.hoodieboi.rainbowquartz.plugin.command.ViewItem
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import me.lucko.commodore.file.CommodoreFileReader
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.PluginCommand
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException

open class RainbowQuartz : JavaPlugin() {

    companion object {
        val itemManager: ItemManager = ItemManager()
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventDispatcher(), this)
        registerCommands()

        generateTestResources()
    }

    @Throws(IOException::class)
    private fun registerCommands() {

        val getItemCommand = server.getPluginCommand("getitem")
        getItemCommand!!.setExecutor(GetItem())
        getItemCommand.tabCompleter = GetItem()
        val viewItemCommand = server.getPluginCommand("viewitem")
        viewItemCommand!!.setExecutor(ViewItem())
        viewItemCommand.tabCompleter = ViewItem()

        // check if brigadier is supported by server
        if (CommodoreProvider.isSupported()) {

            // get a commodore instance
            val commodore = CommodoreProvider.getCommodore(this)

            // register completions for each command
            registerCompletionsFromFile(commodore, getItemCommand)
            registerCompletionsFromFile(commodore, viewItemCommand)
        }
    }

    @Throws(IOException::class)
    private fun registerCompletionsFromFile(commodore: Commodore, command: PluginCommand) {
        val file = getResource("commodore/${command.name}.commodore")
        commodore.register(command, CommodoreFileReader.INSTANCE.parse<Any>(file))
    }

    override fun onDisable() {
        itemManager.clear()
    }

    private fun generateTestResources() {
        // Create temp item
        itemManager.registerItem(ItemBuilder(NamespacedKey(this, "quartz_sword"), Material.IRON_SWORD)
            .setName("Quartz Sword")
            .addEnchant(Enchantment.FIRE_ASPECT)
            .addRecipe(ShapedRecipe("Q", "Q", "S")
                .setIngredient('Q', Material.QUARTZ)
                .setIngredient('S', Material.STICK)
            ).build())

        itemManager.registerItem(ItemBuilder(NamespacedKey.minecraft("super_potato"), Material.BAKED_POTATO, 4)
            .setName(text("Super Potato").color(LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
            .addRecipe(ShapedRecipe("PP", "PP")
                .setIngredient('P', Material.POTATO)
            ).build())

        itemManager.registerItem(ItemBuilder(NamespacedKey(this, "coal_lump"), Material.CHARCOAL)
            .setName("Lump of Coal")
            .addRecipe(SmokingRecipe(Material.SPRUCE_LOG))
            .build())

        itemManager.registerItem(ItemBuilder(NamespacedKey(this, "magic_diamond"), Material.DIAMOND)
            .setName(text("Magic Diamond").color(TextColor.fromHexString("#b26ce9")))
            .addEnchant(Enchantment.KNOCKBACK, 100)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS)
            .addRecipe(SmithingTransformRecipe(Material.DIAMOND, ItemStack(Material.GLASS, 4), Material.QUARTZ))
            .build())

        itemManager.registerItem(ItemBuilder(NamespacedKey(this, "compressed_cobblestone"), Material.COBBLESTONE)
            .setName("Compressed Cobblestone")
            .addRecipe(ShapelessRecipe()
                .addIngredient(ItemStack(Material.COBBLESTONE), 4)
            ).build())
    }
}