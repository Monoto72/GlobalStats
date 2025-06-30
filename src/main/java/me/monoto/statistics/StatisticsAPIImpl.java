package me.monoto.statistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.monoto.statistics.stats.GlobalStatistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class StatisticsAPIImpl implements StatisticsAPI {

    private final Statistics plugin;
    private static final Gson gson = new GsonBuilder().create();

    public StatisticsAPIImpl(Statistics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void incrementCustomCropStat(Player player, String cropId, String displayName, Material material) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "WILD_CROPS_STATS");

        String stored = pdc.get(key, PersistentDataType.STRING);
        JsonObject json;

        try {
            json = stored != null ? gson.fromJson(stored, JsonObject.class) : new JsonObject();
        } catch (JsonSyntaxException e) {
            plugin.getLogger().warning("Malformed JSON in CUSTOM_CROP_STATS for " + player.getName());
            json = new JsonObject();
        }

        JsonObject cropEntry;
        if (json.has(cropId) && json.get(cropId).isJsonObject()) {
            cropEntry = json.getAsJsonObject(cropId);

            int currentCount = cropEntry.has("count") ? cropEntry.get("count").getAsInt() : 0;
            cropEntry.addProperty("count", currentCount + 1);

            if (!displayName.equals(cropEntry.get("name").getAsString())) {
                cropEntry.addProperty("name", displayName);
            }

            if (!material.name().equals(cropEntry.get("material").getAsString())) {
                cropEntry.addProperty("material", material.name());
            }
        } else {
            cropEntry = new JsonObject();
            cropEntry.addProperty("count", 1);
            cropEntry.addProperty("name", displayName);
            cropEntry.addProperty("material", material.name());
        }

        json.add(cropId, cropEntry);
        pdc.set(key, PersistentDataType.STRING, gson.toJson(json));
        PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
        GlobalStatistics globalStats = StatisticsManager.getGlobalStatistics();

        if (stats != null && globalStats != null) {
            stats.setHarvestedCrops(stats.getHarvestedCrops() + 1);
            StatisticsManager.getGlobalStatistics().setHarvestedCrops(StatisticsManager.getGlobalStatistics().getHarvestedCrops() + 1);
        }

        player.setMetadata("WILD_CROPS_BREAK", new FixedMetadataValue(plugin, true));
    }
}
