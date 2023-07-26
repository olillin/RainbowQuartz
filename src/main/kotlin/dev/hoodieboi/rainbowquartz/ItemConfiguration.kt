package dev.hoodieboi.rainbowquartz
//
//import dev.hoodieboi.rainbowquartz.item.Item
//import org.bukkit.NamespacedKey
//import org.bukkit.configuration.ConfigurationSection
//import org.bukkit.configuration.file.YamlConfiguration
//import java.io.Reader
//import java.text.ParseException
//
//class ItemConfiguration : YamlConfiguration() {
//
//    companion object {
//        fun loadConfiguration(reader: Reader): ItemConfiguration {
//            ItemConfiguration().
//            YamlConfiguration.loadConfiguration(reader)
//        }
//    }
//
//    fun loadItems() {
//        for (item in getItems()) {
//            RainbowQuartz.itemManager.registerItem(item)
//        }
//    }
//
//    fun getItems(): Set<Item> {
//        val namespaces: Set<String> = super.getKeys(false)
//        val items = HashSet<Item>()
//        for (namespace in namespaces) {
//            val namespaceConfiguration = super.getConfigurationSection(namespace)
//            namespaceConfiguration
//            items.add()
//        }
//    }
//
////    @Throws(ParseException::class)
////    fun parseItem(namespace: String, configuration: ConfigurationSection): Item {
////        val key = NamespacedKey(namespace, configuration.name)
////        val itemType = configuration.getString("id")
////        val builder = Item.ItemBuilder(configuration.name, configuration.)
////    }
//}