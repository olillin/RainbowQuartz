package dev.hoodieboi.rainbowquartz

import dev.hoodieboi.rainbowquartz.craft.*
import dev.hoodieboi.rainbowquartz.event.ItemEventDispatcher
import dev.hoodieboi.rainbowquartz.event.handler.PlayerPotionEffectEventHandler
import dev.hoodieboi.rainbowquartz.item.ItemBuilder
import dev.hoodieboi.rainbowquartz.item.ItemManager
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import dev.hoodieboi.rainbowquartz.plugin.command.GetItemCommand
import dev.hoodieboi.rainbowquartz.plugin.command.MenuCommand
import dev.hoodieboi.rainbowquartz.plugin.command.ViewItemCommand
import dev.hoodieboi.rainbowquartz.plugin.gui.GuiEventDispatcher
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import me.lucko.commodore.file.CommodoreFileReader
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.IOException
import java.util.*

open class RainbowQuartz : JavaPlugin(), Listener {

    companion object {
        lateinit var itemManager: ItemManager
        lateinit var itemEventDispatcher: ItemEventDispatcher
        lateinit var guiEventDispatcher: GuiEventDispatcher
    }

    override fun onEnable() {
        // Initialize event dispatchers
        itemEventDispatcher = ItemEventDispatcher(this)
        guiEventDispatcher = GuiEventDispatcher(this)
        guiEventDispatcher.start()

        itemManager = ItemManager(this)
        itemManager.reload()

        // Initialize commands
        registerCommands()

        generateTestResources()
    }

    override fun onLoad() {
        ConfigurationSerialization.registerClass(Recipe::class.java)
        ConfigurationSerialization.registerClass(BlastingRecipe::class.java)
        ConfigurationSerialization.registerClass(CampfireRecipe::class.java)
        ConfigurationSerialization.registerClass(FurnaceRecipe::class.java)
        ConfigurationSerialization.registerClass(ShapedRecipe::class.java)
        ConfigurationSerialization.registerClass(ShapelessRecipe::class.java)
        ConfigurationSerialization.registerClass(SmithingTransformRecipe::class.java)
        ConfigurationSerialization.registerClass(SmokingRecipe::class.java)
        ConfigurationSerialization.registerClass(StonecuttingRecipe::class.java)
    }

    @Suppress("SpellCheckingInspection")
    @Throws(IOException::class)
    private fun registerCommands() {

        val getItemCommand = server.getPluginCommand("getitem")
        var executor: TabExecutor = GetItemCommand()
        getItemCommand!!.setExecutor(executor)
        getItemCommand.tabCompleter = executor

        val viewItemCommand = server.getPluginCommand("viewitem")
        executor = ViewItemCommand()
        viewItemCommand!!.setExecutor(executor)
        viewItemCommand.tabCompleter = executor

        val menuCommand = server.getPluginCommand("rainbowquartz")
        executor = MenuCommand(this)
        menuCommand!!.setExecutor(executor)
        menuCommand.tabCompleter = executor

        // check if brigadier is supported by server
        if (CommodoreProvider.isSupported()) {

            // get a commodore instance
            val commodore = CommodoreProvider.getCommodore(this)

            // register completions for each command
            registerCompletionsFromFile(commodore, getItemCommand)
            registerCompletionsFromFile(commodore, viewItemCommand)
        }
    }

    private fun registerCompletionsFromFile(commodore: Commodore, command: PluginCommand) {
        val uri = "commodore/${command.name}.commodore"
        val file = getResource(uri)
        if (file == null) {
            logger.warning("Unable to register completions for command ${command.name}: Could not find file $uri")
        }
        commodore.register(command, CommodoreFileReader.INSTANCE.parse<Any>(file))
    }

    override fun onDisable() {
        itemManager.clear()
    }

    private fun generateTestResources() {
        // Create temp item
        itemManager.registerDefault(ItemBuilder(NamespacedKey(this, "quartz_sword"), Material.IRON_SWORD)
            .setName("Quartz Sword")
            .addEnchant(Enchantment.FIRE_ASPECT)
            .addRecipe(ShapedRecipe("Q", "Q", "S")
                .setIngredient('Q', Material.QUARTZ)
                .setIngredient('S', Material.STICK)
            ).build())

        itemManager.registerDefault(ItemBuilder(NamespacedKey.fromString("foo:super_potato")!!, Material.BAKED_POTATO)
            .setName(text("Super Potato").color(LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
            .addRecipe(ShapedRecipe("PP", "PP")
                    .setIngredient('P', Material.POTATO)
            ).addEventHandler(
                PlayerDropItemEvent::class.java,
                { event ->
                    event.itemDrop.itemStack onlyIf { it.itemMeta.rainbowQuartzId == NamespacedKey.fromString("foo:super_potato")!! }
                },
                PlayerPotionEffectEventHandler(
                        PotionEffect(PotionEffectType.LEVITATION, 200, 0)
                )
            ).build())

        itemManager.registerDefault(ItemBuilder(NamespacedKey(this, "coal_lump"), Material.CHARCOAL)
            .setName("Lump of Coal")
            .addRecipe(SmokingRecipe(Material.SPRUCE_LOG))
            .build())

        itemManager.registerDefault(ItemBuilder(NamespacedKey(this, "magic_diamond"), Material.DIAMOND)
            .setName(text("Magic Diamond").color(TextColor.fromHexString("#b26ce9")))
            .addEnchant(Enchantment.KNOCKBACK, 100)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS)
            .addRecipe(SmithingTransformRecipe(Material.DIAMOND, ItemStack(Material.GLASS, 4), Material.QUARTZ))
            .build())

        itemManager.registerDefault(ItemBuilder(NamespacedKey(this, "compressed_cobblestone"), Material.COBBLESTONE)
            .setName("Compressed Cobblestone")
            .addRecipe(ShapelessRecipe()
                .addIngredient(ItemStack(Material.COBBLESTONE), 4)
            ).build())

        itemManager.registerDefault(ItemBuilder(NamespacedKey(this, "boots_of_the_chicken"), Material.GOLDEN_BOOTS)
            .setName(text("Boots of the Chicken").color(YELLOW))
            .addRecipe(ShapedRecipe("F F", "L L")
                .setIngredient('F', Material.FEATHER)
                .setIngredient('L', Material.LEATHER))
            .addAttributeModifier(
                Attribute.GENERIC_MOVEMENT_SPEED,
                AttributeModifier(UUID.randomUUID(), "boots_of_the_chicken", 1.2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET))
            .build())
    }

    @EventHandler
    fun onDropSuperPotato(event: PlayerDropItemEvent) {
        event.player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 100, 0))
    }
}