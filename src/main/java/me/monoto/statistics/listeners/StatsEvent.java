package me.monoto.statistics.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.GlobalStatistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.BlockCache;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.bukkit.Bukkit.getPluginManager;

public class StatsEvent implements Listener {

    Statistics plugin;
    private static final Gson gson = new Gson();
    private static final Set<Material> allowedCropsSet = new HashSet<>(BlockCache.getAllowedCrops());

    public StatsEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
        this.plugin = main;
    }

    @EventHandler
    public void fishing(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getCaught() instanceof Item item && event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Material fishable = item.getItemStack().getType();

            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
            if (stats != null) {
                stats.setFishedFish(stats.getFishedFish() + 1);
                StatisticsManager.getGlobalStatistics().setFishedFish(
                        StatisticsManager.getGlobalStatistics().getFishedFish() + 1
                );

                PersistentDataContainer pdc = player.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(this.plugin, "FISHING_STATS");

                String stored = pdc.get(key, PersistentDataType.STRING);
                JsonObject json;

                try {
                    json = stored != null ? gson.fromJson(stored, JsonObject.class) : new JsonObject();
                } catch (JsonSyntaxException e) {
                    json = new JsonObject(); // fallback if corrupted
                }

                String fishName = fishable.name();
                if (json.has(fishName)) {
                    int current = json.get(fishName).getAsInt();
                    json.addProperty(fishName, current + 1);
                } else {
                    json.addProperty(fishName, 1);
                }

                pdc.set(key, PersistentDataType.STRING, gson.toJson(json));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void breaking(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("WILD_CROPS_BREAK")) {
            player.removeMetadata("WILD_CROPS_BREAK", plugin);
            return;
        }

        Block block = event.getBlock();
        PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
        GlobalStatistics globalStats = StatisticsManager.getGlobalStatistics();

        if (stats != null && globalStats != null) {
            if (block.getType().isAir()) return;

            if (isVanillaCrop(block)) {
                stats.setHarvestedCrops(stats.getHarvestedCrops() + 1);
                StatisticsManager.getGlobalStatistics().setHarvestedCrops(StatisticsManager.getGlobalStatistics().getHarvestedCrops() + 1);
            } else {
                stats.setMinedBlocks(stats.getMinedBlocks() + 1);
                StatisticsManager.getGlobalStatistics().setMinedBlocks(StatisticsManager.getGlobalStatistics().getMinedBlocks() + 1);
            }
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

    private boolean isVanillaCrop(Block block) {
        Material type = block.getType();

        if (!allowedCropsSet.contains(type)) {
            return false;
        }

        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() == ageable.getMaximumAge();
        }

        return true;
    }
}
