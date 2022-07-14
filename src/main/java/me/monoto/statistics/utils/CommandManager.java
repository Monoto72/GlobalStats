package me.monoto.statistics.utils;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.commands.AllPlayersCommand;
import me.monoto.statistics.commands.ForceUpdateCommand;
import me.monoto.statistics.commands.GlobalStatisticsCommand;
import me.monoto.statistics.commands.PlayerSearchCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CommandManager {

    public static void setupCommandManager(Statistics plugin) {
        BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

        commandManager.registerSuggestion(SuggestionKey.of("online-players"), (CommandSender sender, SuggestionContext context) -> Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid Usage!"));
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: /stats " + context.getSubCommand()));

        commandManager.registerCommand(
                new GlobalStatisticsCommand(plugin),
                new PlayerSearchCommand(plugin),
                new GlobalStatisticsCommand(plugin),
                new AllPlayersCommand(plugin),
                new ForceUpdateCommand(plugin)
        );
    }
}
