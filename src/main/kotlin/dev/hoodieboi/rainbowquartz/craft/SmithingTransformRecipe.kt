package dev.hoodieboi.rainbowquartz.craft

import dev.hoodieboi.rainbowquartz.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice

class SmithingTransformRecipe(base: RecipeChoice, addition: RecipeChoice, var template: RecipeChoice) : SmithingRecipe(base, addition) {

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

    override fun toBukkitRecipe(item: Item): org.bukkit.inventory.SmithingTransformRecipe {
        return org.bukkit.inventory.SmithingTransformRecipe(
            NamespacedKey.fromString(item.key.toString() + ".smithing_transform")!!,
            item.result,
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
}