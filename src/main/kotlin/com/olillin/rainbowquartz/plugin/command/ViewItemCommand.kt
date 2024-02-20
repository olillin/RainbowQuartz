package com.olillin.rainbowquartz.plugin.command

import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.olillin.rainbowquartz.RainbowQuartz
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.event.ClickEvent.suggestCommand
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ViewItemCommand : TabExecutor {
    companion object {
        val completion: LiteralArgumentBuilder<String> = literal<String>("viewitem")
            .then(argument("item", word()))
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() || args.size > 1) {
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

        val itemStack = item.getItem()
        sender.sendMessage(text("Item Preview ").append(
                itemStack.displayName()
                    .hoverEvent(itemStack)
                    .clickEvent(suggestCommand("/getitem ${item.key}"))
        ))
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