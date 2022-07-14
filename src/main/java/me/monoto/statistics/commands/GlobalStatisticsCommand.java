package me.monoto.statistics.commands;

import dev.triumphteam.cmd.core.annotation.Default;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.GlobalMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GlobalStatisticsCommand extends MainCommand {

    static Statistics plugin;

    public GlobalStatisticsCommand(@NotNull final Statistics main) {
        plugin = main;
    }

    @Default
    public void executor(CommandSender sender) {
        if (sender instanceof Player) {
                GlobalMenu.initialise((Player) sender);
        } else {
            sender.sendMessage("[GlobalStats] by Monoto#0001. Version: " + plugin.getDescription().getVersion());
        }
    }
}
