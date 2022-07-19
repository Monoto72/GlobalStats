package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import static org.bukkit.Bukkit.getPluginManager;

public class JoinEvent implements Listener {

    Statistics plugin;

    public JoinEvent(Statistics plugin){
        getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
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
            player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            "<black>[<white>GlobalStats<black>] <aqua><version> is out! (<u><click:open_url:'https://www.spigotmc.org/resources/globalstats.103379/'>Download</click></u>)",
                            Placeholder.component("version", Component.text(Statistics.getInstance().getVersion()))
                    )
            );
        }
    }
}
