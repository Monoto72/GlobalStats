package me.monoto.statistics;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface StatisticsAPI {
    void incrementCustomCropStat(Player player, String cropId, String displayName, Material material);
}
