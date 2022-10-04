package me.monoto.statistics.utils;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.commands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CommandManager {

    public static void setupCommandManager(Statistics plugin) {
        BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

        commandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getLanguage().getString("error.invalid_usage", "<red>Invalid usage")
            ));
        });
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getLanguage().getString("error.unknown_command", "<red>Unknown command"),
                    Placeholder.component("cmd", Component.text("/stats " + context.getSubCommand())))
            );
        });
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getLanguage().getString("error.no_permission", "<red>No permission"),
                    Placeholder.component("permission", Component.text())
            ));
        });

        commandManager.registerCommand(
                new GlobalStatisticsCommand(plugin),
                new PlayerSearchCommand(plugin),
                new GlobalStatisticsCommand(plugin),
                new AllPlayersCommand(plugin),
                new ForceUpdateCommand(plugin),
                new ReloadCommand(plugin)
        );
    }
}