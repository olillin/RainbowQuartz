package com.olillin.rainbowquartz.plugin.command

import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.olillin.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class GetItemCommand : TabExecutor {
    companion object {
        val completion: LiteralArgumentBuilder<String> = literal<String>("getitem")
            .then(argument<String, String>("item", word())
            .then(argument("amount", integer(1))))
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(text("Command is intended only for use by players"))
            return true
        }

        if (args.isEmpty() || args.size > 2) {
            return false
        }

        val key = NamespacedKey.fromString(args[0])
        if (key == null) {
            sender.sendMessage(translatable("argument.id.invalid").color(RED))
            return true
        }
        val item = RainbowQuartz.itemManager.getItem(key)
        if (item == null) {
            sender.sendMessage(translatable("argument.item.id.invalid", text(args[0])).color(RED))
            return true
        }

        val itemStack = ItemStack(item.item)
        var amount = 1
        if (args.size >= 2) {
            val parsed = args[1].toIntOrNull()
            if (parsed == null || parsed < 1) {
                sender.sendMessage(text("Invalid amount ${args[1]}"))
                return true
            }
            val maxItems = itemStack.type.maxStackSize * 100
            if (parsed > maxItems) {
                sender.sendMessage(
                    translatable(
                        "commands.give.failed.toomanyitems",
                        text(maxItems),
                        itemStack.displayName().hoverEvent(itemStack)
                    ).color(RED)
                )
                return true
            }
            amount = parsed.toInt()
        }

        sender.sendMessage(
            translatable(
                "commands.give.success.single",
                text(amount),
                itemStack.displayName()
                    .hoverEvent(itemStack)
                    .clickEvent(ClickEvent.suggestCommand("/getitem ${item.key}")),
                sender.teamDisplayName()
            )
        )
        while (amount > 0) {
            itemStack.amount = min(amount, itemStack.type.maxStackSize)
            sender.inventory.addItem(itemStack)
            amount -= itemStack.amount
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return if (args.size == 1) {
            RainbowQuartz.itemManager.itemKeys.map { it.toString() }.toMutableList()
        } else {
            ArrayList()
        }
    }
}