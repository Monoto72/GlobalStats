package me.monoto.statistics.commands;

import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.PlayerMenu;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.Formatters;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSearchCommand extends MainCommand {

    static Statistics plugin;

    public PlayerSearchCommand(@NotNull final Statistics main) {
        plugin = main;
    }

    @SubCommand("check")
    public void executor(CommandSender sender, @Suggestion("online-players") String name) {
        Player target = Bukkit.getPlayer(name);

        if (target == null) {
            sender.sendMessage(name + " is not online");
            return;
        }
        String formattedName = Formatters.getPossessionString(target.getName());

        if (sender instanceof Player) {
            PlayerMenu.initialise((Player) sender, target);
        } else {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(target.getUniqueId());

            plugin.getLogger().info(formattedName + " Statistics");
            plugin.getLogger().info("Total Fished: " + stats.getFishedFish());
            plugin.getLogger().info("Total Mined: " + stats.getMinedBlocks());
            plugin.getLogger().info("Total Killed: " + stats.getMobsKilled());
            plugin.getLogger().info("Total Traversed: " + stats.getTraversedBlocks());
            plugin.getLogger().info("Total Placed " + stats.getPlacedBlocks());
        }
    }
}
