package me.monoto.statistics.commands;

import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.GlobalMenu;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
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
                OfflinePlayer oPlayer = Bukkit.getOfflinePlayerIfCached(name);
                    if (oPlayer != null) {
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
                                GlobalMenu.initialise((Player) sender, oPlayer);
                                // Potentially add insert if not exist and update if exist
                            } else {
                                GlobalMenu.initialise((Player) sender, oPlayer);
                            }
                        } else {
                            this.plugin.getLogger().info("Too many statistics for you to view in console.");
                        }
                    } else {
                        sender.sendMessage(Formatters.mini(Formatters.lang().getString("error.never_played", "<player> has never played"), "player", Component.text(name)));
                    }
        } else {
            if (sender instanceof Player) {
                GlobalMenu.initialise((Player) sender, target);
            } else {
                this.plugin.getLogger().info("Too many statistics for you to view in console.");
            }
        }
    }
}
