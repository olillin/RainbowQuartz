package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

class SmithingTransformRecipe(base: RecipeChoice, addition: RecipeChoice, var template: RecipeChoice) : SmithingRecipe(base, addition) {
    override val suffix: String
        get() = id

    companion object {
        const val id = "smithing_transform"

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(args: Map<String, Any>): SmithingTransformRecipe {

            val section = MemoryConfiguration()
            section.addDefaults(args)

            val base: ItemStack = section.getItemStack("base") ?: throw IllegalArgumentException("Invalid or missing property 'base'")
            val addition: ItemStack = section.getItemStack("addition") ?: throw IllegalArgumentException("Invalid or missing property 'addition'")
            val template: ItemStack = section.getItemStack("template") ?: throw IllegalArgumentException("Invalid or missing property 'template'")
            val recipe = SmithingTransformRecipe(base, addition, template)

            val group = section.getString("group")
                ?: throw IllegalArgumentException("Invalid value for property 'group'")
            recipe.setGroup(group)

            return recipe
        }
    }

    constructor(base: Material, addition: RecipeChoice) : this(MaterialChoice(base), addition, Material.AIR)
    constructor(base: ItemStack, addition: RecipeChoice) : this(ExactChoice(base), addition, Material.AIR)
    constructor(base: RecipeChoice, addition: Material) : this(base, MaterialChoice(addition), Material.AIR)
    constructor(base: Material, addition: Material) : this(base, MaterialChoice(addition), Material.AIR)
    constructor(base: ItemStack, addition: Material) : this(base, MaterialChoice(addition), Material.AIR)
    constructor(base: RecipeChoice, addition: ItemStack) : this(base, ExactChoice(addition), Material.AIR)
    constructor(base: Material, addition: ItemStack) : this(base, ExactChoice(addition), Material.AIR)
    constructor(base: ItemStack, addition: ItemStack) : this(base, ExactChoice(addition), Material.AIR)
    constructor(base: Material, addition: RecipeChoice, template: RecipeChoice) : this(MaterialChoice(base), addition, template)
    constructor(base: ItemStack, addition: RecipeChoice, template: RecipeChoice) : this(ExactChoice(base), addition, template)
    constructor(base: RecipeChoice, addition: Material, template: RecipeChoice) : this(base, MaterialChoice(addition), template)
    constructor(base: Material, addition: Material, template: RecipeChoice) : this(base, MaterialChoice(addition), template)
    constructor(base: ItemStack, addition: Material, template: RecipeChoice) : this(base, MaterialChoice(addition), template)
    constructor(base: RecipeChoice, addition: ItemStack, template: RecipeChoice) : this(base, ExactChoice(addition), template)
    constructor(base: Material, addition: ItemStack, template: RecipeChoice) : this(base, ExactChoice(addition), template)
    constructor(base: ItemStack, addition: ItemStack, template: RecipeChoice) : this(base, ExactChoice(addition), template)
    constructor(base: RecipeChoice, addition: RecipeChoice, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: Material, addition: RecipeChoice, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: ItemStack, addition: RecipeChoice, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: RecipeChoice, addition: Material, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: Material, addition: Material, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: ItemStack, addition: Material, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: RecipeChoice, addition: ItemStack, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: Material, addition: ItemStack, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: ItemStack, addition: ItemStack, template: Material) : this(base, addition, MaterialChoice(template))
    constructor(base: RecipeChoice, addition: RecipeChoice, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: Material, addition: RecipeChoice, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: ItemStack, addition: RecipeChoice, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: RecipeChoice, addition: Material, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: Material, addition: Material, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: ItemStack, addition: Material, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: RecipeChoice, addition: ItemStack, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: Material, addition: ItemStack, template: ItemStack) : this(base, addition, ExactChoice(template))
    constructor(base: ItemStack, addition: ItemStack, template: ItemStack) : this(base, addition, ExactChoice(template))

    override fun asBukkitRecipe(item: Item): org.bukkit.inventory.SmithingTransformRecipe {
        return org.bukkit.inventory.SmithingTransformRecipe(
            key(item),
            item.item,
            template,
            base,
            addition
        )
    }

    fun setTemplate(template: RecipeChoice): SmithingRecipe {
        this.template = template
        return this
    }

    fun setTemplate(template: Material): SmithingRecipe {
        return setBase(MaterialChoice(template))
    }

    fun setTemplate(template: ItemStack): SmithingRecipe {
        return setBase(ExactChoice(template))
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "group" to group,
            "base" to base.itemStack,
            "addition" to addition.itemStack,
            "template" to template.itemStack
        )
    }
}