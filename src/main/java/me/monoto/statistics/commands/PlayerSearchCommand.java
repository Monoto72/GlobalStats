package me.monoto.statistics.commands;

import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.PlayerMenu;
import me.monoto.statistics.stats.StatisticsManager;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSearchCommand extends MainCommand {

    Statistics plugin;

    public PlayerSearchCommand(@NotNull final Statistics main) {
        plugin = main;
    }

    @SubCommand("check")
    public void executor(CommandSender sender, @Suggestion("online-players") String name) {
        Player target = Bukkit.getPlayer(name);

        if (target == null) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(name);

                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    if (oPlayer.hasPlayedBefore()) {
                        if (sender instanceof Player) {
                            if (StatisticsManager.getOfflinePlayerStatistics().get(oPlayer.getUniqueId()) == null) {
                                StatisticsManager.setOfflinePlayerStatistics(oPlayer.getUniqueId(),
                                        oPlayer.getName(),
                                        oPlayer.getStatistic(Statistic.FISH_CAUGHT),
                                        StatisticsManager.getTotalBlocks(oPlayer, Statistic.MINE_BLOCK),
                                        oPlayer.getStatistic(Statistic.MOB_KILLS),
                                        StatisticsManager.getTotalBlocks(oPlayer, Statistic.USE_ITEM),
                                        StatisticsManager.getTotalBlocksTraversed(oPlayer)
                                );
                                PlayerMenu.initialise((Player) sender, oPlayer);
                                // Potentially add insert if not exist and update if exist
                            } else {
                                PlayerMenu.initialise((Player) sender, oPlayer);
                            }
                        } else {
                            this.plugin.getLogger().info("Too many statistics for you to view in console.");
                        }
                    } else {
                        sender.sendMessage(oPlayer.getName() + " has never played the server before.");
                    }
                });
            });
        } else {
            if (sender instanceof Player) {
                PlayerMenu.initialise((Player) sender, target);
            } else {
                this.plugin.getLogger().info("Too many statistics for you to view in console.");
            }
        }
    }
}
