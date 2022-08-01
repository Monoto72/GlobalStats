package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Objects;

import static org.bukkit.Bukkit.getPluginManager;

public class StatsEvent implements Listener {

    Statistics plugin;

    public StatsEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
        this.plugin = main;
    }

    @EventHandler
    public void fishing(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getCaught() != null && event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Material fishable = ((Item) event.getCaught()).getItemStack().getType();

            if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());

                stats.setFishedFish(stats.getFishedFish() + 1);
                StatisticsManager.getGlobalStatistics().setFishedFish(StatisticsManager.getGlobalStatistics().getFishedFish() + 1);

                PersistentDataContainer pdc = player.getPersistentDataContainer();

                JSONParser parser = new JSONParser();
                JSONObject json;

                try {
                    json = (JSONObject) parser.parse(pdc.get(new NamespacedKey(this.plugin, "FISHING_STATS"), PersistentDataType.STRING));
                } catch (Exception exception) {
                    json = new JSONObject();
                }

                if (json.get(fishable.name()) != null) {
                    json.replace(fishable.name(), ((Long) json.get(fishable.name())).intValue() + 1);
                } else {
                    json.put(fishable.name(), 1);
                }

                pdc.set(new NamespacedKey(this.plugin, "FISHING_STATS"), PersistentDataType.STRING, json.toString());
            }
        }
    }

    @EventHandler
    public void breaking(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
            stats.setMinedBlocks(stats.getMinedBlocks() + 1);

            StatisticsManager.getGlobalStatistics().setMinedBlocks(StatisticsManager.getGlobalStatistics().getMinedBlocks() + 1);
        }
    }

    @EventHandler
    public void killing(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
                stats.setMobsKilled(stats.getMobsKilled() + 1);

                StatisticsManager.getGlobalStatistics().setMobsKilled(StatisticsManager.getGlobalStatistics().getMobsKilled() + 1);
            }
        }
    }

    @EventHandler
    public void moving(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());

            if (stats.getTraversedBlocks() <= StatisticsManager.getTotalBlocksTraversed(player)) {
                stats.setTraversedBlocks(StatisticsManager.getTotalBlocksTraversed(player));
            }
        }
    }

    @EventHandler
    public void placing(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
            stats.setPlacedBlocks(stats.getPlacedBlocks() + 1);

            StatisticsManager.getGlobalStatistics().setPlacedBlocks(StatisticsManager.getGlobalStatistics().getPlacedBlocks() + 1);
        }
    }
}
