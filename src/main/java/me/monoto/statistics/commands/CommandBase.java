package me.monoto.statistics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.GlobalMenu;
import me.monoto.statistics.utils.ComponentUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.Force;

import java.util.Map;

public class CommandBase {

    public CommandBase(Statistics plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Map<String, String> usage = Map.of(
                    "", "Displays global statistics amount",
                    "help", "Shows this help menu",
                    "reload", "Reloads configuration and data",
                    "all", "Displays statistics of every player",
                    "check", "Displays statistics of chosen player",
                    "update", "Force updates the statistics of CCU"
            );

            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("stats")
                    .executes(CommandBase::openGui)
                    .then(HelpCommand.builder(usage))
                    .then(ReloadCommand.builder())
                    .then(AllPlayersCommand.builder())
                    .then(PlayerSearchCommand.builder())
                    .then(ForceUpdateCommand.builder());

            event.registrar().register(root.build());
        });
    }

    private static int openGui(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        CommandSender sender = src.getSender();
        Entity exec = src.getExecutor();

        if (!(exec instanceof Player)) {
            sender.sendMessage(ComponentUtils.error("Only players can use this command!"));
            return Command.SINGLE_SUCCESS;
        }

        GlobalMenu.initialise((Player) sender);
        return Command.SINGLE_SUCCESS;
    }
}
