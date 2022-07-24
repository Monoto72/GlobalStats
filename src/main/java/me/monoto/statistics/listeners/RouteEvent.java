package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import static org.bukkit.Bukkit.getPluginManager;

public class RouteEvent implements Listener {

    Statistics plugin;

    public RouteEvent(Statistics plugin){
        getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        DatabaseManager.getPlayer(player.getUniqueId());
        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) == null) {
            StatisticsManager.setPlayerStatistics(
                    player.getUniqueId(),
                    player.getName(),
                    player.getStatistic(Statistic.FISH_CAUGHT),
                    StatisticsManager.getTotalBlocks(player, Statistic.MINE_BLOCK),
                    player.getStatistic(Statistic.MOB_KILLS),
                    StatisticsManager.getTotalBlocks(player, Statistic.USE_ITEM),
                    StatisticsManager.getTotalBlocksTraversed(player)
            );

            DatabaseManager.savePlayer(player.getUniqueId());
        }  else { // Changed name catch
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());

            if (stats != null) {
                if (!stats.getPlayerName().equals(player.getName())) {
                    DatabaseManager.updatePlayerName(stats.getPlayerName(), stats.getPlayerUUID());
                }
            }
        }
        
        if (player.isOp() && Statistics.getInstance().getConfig().getBoolean("check-for-updates") && Statistics.getInstance().getVersion() != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            "<black>[<white>GlobalStats<black>] <aqua><version> is out! (<u><click:open_url:'https://www.spigotmc.org/resources/globalstats.103379/'>Download</click></u>)",
                            Placeholder.component("version", Component.text(Statistics.getInstance().getVersion()))
                    )
            ), 20L * 2);
        }

        if (player.getName().equalsIgnoreCase("Monotoo") || player.getName().equalsIgnoreCase("Monoto") || player.getName().equalsIgnoreCase("oiSam")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            "<black>[<white>GlobalStats<black>] <aqua>This server is running your plugin. It is running version <version>",
                            Placeholder.component("version", Component.text(plugin.getServer().getVersion()))
                    )
            ), 20L * 2);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) != null) {
            DatabaseManager.updatePlayer(event.getPlayer().getUniqueId());
            StatisticsManager.getPlayerStatistics().remove(event.getPlayer().getUniqueId());
        }
    }
}
